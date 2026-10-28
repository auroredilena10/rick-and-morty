package org.mathieu.data.repositories

import org.mathieu.data.local.CharacterLocal
import org.mathieu.data.local.LocationLocal
import org.mathieu.data.local.objects.CharacterObject
import org.mathieu.data.local.objects.LocationObject
import org.mathieu.data.local.objects.toModel
import org.mathieu.data.local.objects.toPreview
import org.mathieu.data.local.objects.toRealmObject
import org.mathieu.data.remote.CharacterApi
import org.mathieu.data.remote.LocationApi
import org.mathieu.data.remote.responses.CharacterResponse
import org.mathieu.domain.models.location.Location
import org.mathieu.domain.models.location.LocationPreview
import org.mathieu.domain.repositories.LocationRepository

/**
 * Lit les lieux en base d'abord et ne demande à l'API que ce qui n'a jamais été enregistré.
 *
 * Les résidents passent par [CharacterApi] et [CharacterLocal] plutôt que par `CharacterRepository` :
 * celui-ci dépend déjà de [LocationRepository] pour remplir `Character.location`, et Koin ne sait pas
 * résoudre deux classes qui ont besoin l'une de l'autre.
 *
 * @param locationApi service distant des lieux.
 * @param locationLocal accès aux lieux enregistrés dans Realm.
 * @param characterApi service distant des personnages, pour les résidents absents de la base.
 * @param characterLocal accès aux personnages enregistrés dans Realm.
 */
internal class LocationRepositoryImpl(
    private val locationApi: LocationApi,
    private val locationLocal: LocationLocal,
    private val characterApi: CharacterApi,
    private val characterLocal: CharacterLocal
) : LocationRepository {

    /**
     * Donne le lieu complet. Chaque résident reçoit ce lieu comme dernier lieu connu :
     * l'API définit justement les résidents comme les personnages vus en dernier ici.
     *
     * @param id identifiant unique du lieu.
     * @return le [Location] avec ses résidents.
     * @throws Exception si le lieu est introuvable en base comme sur l'API.
     */
    override suspend fun getLocation(id: Int): Location {
        val location = getLocationObject(id)
        val preview = location.toPreview()
        val residents = getResidents(location.residentIds.toList())
            .map { it.toModel(location = preview) }
        return location.toModel(residents)
    }

    override suspend fun getLocationPreview(id: Int): LocationPreview =
        getLocationObject(id).toPreview()


    /**
     * Récupère le lieu en suivant ces étapes :
     * 1. Le cherche en base.
     * 2. S'il n'y est pas, le demande à l'API.
     * 3. Si l'API le renvoie, l'enregistre en base avant de le rendre.
     * 4. Sinon, lève une exception.
     *
     * @param id identifiant unique du lieu.
     * @return le [LocationObject] enregistré.
     * @throws Exception si le lieu est introuvable en base comme sur l'API.
     */
    private suspend fun getLocationObject(id: Int): LocationObject =
        locationLocal.getLocation(id)
            ?: locationApi.getLocation(id = id)?.let { response ->
                val obj = response.toRealmObject()
                locationLocal.insert(obj)
                obj
            }
            ?: throw Exception("Location not found.")

    /**
     * Lit les résidents déjà en base, puis demande tous les autres à l'API en un seul appel
     * et les enregistre, pour que la prochaine ouverture du lieu se passe du réseau.
     *
     * @param ids identifiants des résidents du lieu.
     * @return les [CharacterObject] des résidents, triés par identifiant pour garder le même ordre
     * qu'ils viennent de la base ou de l'API.
     */
    private suspend fun getResidents(ids: List<Int>): List<CharacterObject> {
        val saved = ids.mapNotNull { characterLocal.getCharacter(it) }
        val missingIds = ids - saved.map { it.id }.toSet()

        val fetched = if (missingIds.isEmpty()) emptyList()
        else characterApi.getCharacters(missingIds).map(transform = CharacterResponse::toRealmObject)

        characterLocal.saveCharacters(fetched)

        return (saved + fetched).sortedBy { it.id }
    }

}
