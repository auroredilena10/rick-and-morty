package org.mathieu.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.mathieu.data.local.CharacterLocal
import org.mathieu.data.local.objects.CharacterObject
import org.mathieu.data.local.objects.toModel
import org.mathieu.data.local.objects.toRealmObject
import org.mathieu.data.remote.CharacterApi
import org.mathieu.data.remote.responses.CharacterResponse
import org.mathieu.domain.repositories.CharacterRepository
import org.mathieu.domain.repositories.LocationRepository
import org.mathieu.domain.models.character.Character

private const val CHARACTER_PREFS = "character_repository_preferences"
private val nextPage = intPreferencesKey("next_characters_page_to_load")

private val Context.dataStore by preferencesDataStore(
    name = CHARACTER_PREFS
)

/**
 * Remplit le contrat [CharacterRepository] : il lit Realm d'abord, et l'API ne sert qu'à charger
 * la page suivante ou un personnage jamais enregistré.
 *
 * @param context sert à ouvrir le DataStore qui retient la prochaine page à charger.
 * @param characterApi Service des personnages.
 * @param characterLocal DAO des personnages.
 * @param locationRepository donne le dernier lieu connu de chaque personnage, parce que la table
 * des personnages n'en garde que l'id.
 */
internal class CharacterRepositoryImpl(
    private val context: Context,
    private val characterApi: CharacterApi,
    private val characterLocal: CharacterLocal,
    private val locationRepository: LocationRepository
) : CharacterRepository {

    override suspend fun getCharacters(): Flow<List<Character>> =
        characterLocal
            .getCharacters()
            .also {
                if (it.first().isEmpty()) {
                    // Base vide (premier lancement, ou Realm vidé par un changement de version) : on repart de la page 1.
                    // Sinon le DataStore, qui n'est pas vidé avec Realm, ferait charger la page 2 et la 1 manquerait.
                    context.dataStore.edit { prefs -> prefs.remove(nextPage) }
                    fetchNext()
                }
            }
            .mapElement { it.toModelWithLocation() }


    /**
     * Fetches the next batch of characters and saves them to local storage.
     *
     * This function works as follows:
     * 1. Reads the next page number from the data store.
     * 2. If there's a valid next page (i.e., page is not -1), it fetches characters from the API for that page.
     * 3. Extracts the next page number from the API response and updates the data store with it.
     * 4. Transforms the fetched character data into their corresponding realm objects.
     * 5. Saves the transformed realm objects to the local database.
     *
     * Note: If the `next` attribute from the API response is null or missing, the page number is set to -1, indicating there's no more data to fetch.
     */
    private suspend fun fetchNext() {

        val page = context.dataStore.data.map { prefs -> prefs[nextPage] }.first()

        if (page != -1) {

            val response = characterApi.getCharacters(page)

            val nextPageToLoad = response.info.next?.split("?page=")?.last()?.toInt() ?: -1

            context.dataStore.edit { prefs -> prefs[nextPage] = nextPageToLoad }

            val objects = response.results.map(transform = CharacterResponse::toRealmObject)

            characterLocal.saveCharacters(objects)
        }

    }


    override suspend fun loadMore() = fetchNext()


    /**
     * Retrieves the character with the specified ID.
     *
     * The function follows these steps:
     * 1. Tries to fetch the character from the local storage.
     * 2. If not found locally, it fetches the character from the API.
     * 3. Upon successful API retrieval, it saves the character to local storage.
     * 4. If the character is still not found, it throws an exception.
     *
     * @param id The unique identifier of the character to retrieve.
     * @return The [Character] object representing the character details.
     * @throws Exception If the character cannot be found both locally and via the API.
     */
    override suspend fun getCharacter(id: Int): Character =
        characterLocal.getCharacter(id)?.toModelWithLocation()
            ?: characterApi.getCharacter(id = id)?.let { response ->
                val obj = response.toRealmObject()
                characterLocal.insert(obj)
                obj.toModelWithLocation()
            }
            ?: throw Exception("Character not found.")

    /**
     * Mappe le personnage en ajoutant son dernier lieu connu, que [LocationRepository] cherche en base d'abord.
     * La liste et le détail passent tous les deux par ici.
     *
     * Ce que ça coûte : la première fois qu'une page s'affiche, un appel par lieu pas encore connu.
     * Après, tout vient de Realm. Si le réseau plante, l'erreur remonte au lieu de mettre `null` :
     * `null` veut déjà dire « lieu inconnu », l'écran afficherait une info fausse.
     *
     * @receiver le personnage lu en base.
     * @return le [Character], avec `location` à `null` si l'API ne connaît pas ce lieu (id -1).
     */
    private suspend fun CharacterObject.toModelWithLocation(): Character = toModel(
        location = if (locationId == -1) null else locationRepository.getLocationPreview(locationId)
    )

}
