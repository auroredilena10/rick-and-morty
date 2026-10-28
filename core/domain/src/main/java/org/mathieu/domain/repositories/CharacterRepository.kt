package org.mathieu.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.mathieu.domain.models.character.Character

/**
 * Contrat pour obtenir les personnages : le Domain dit ce dont l'application a besoin,
 * la couche Data décide d'où ça vient. Les ViewModels ne dépendent que de cette interface,
 * ce qui permet de changer de stockage ou de la remplacer par une fausse implémentation sans les toucher.
 */
interface CharacterRepository {
    /**
     * Fetches a list of characters from the data source. The function streams the results
     * as a [Flow] of [List] of [Character] objects.
     *
     * @return A flow emitting a list of characters.
     */
    suspend fun getCharacters(): Flow<List<Character>>

    /**
     * Loads more characters from the data source, usually used for pagination purposes.
     * This function typically fetches the next set of characters and appends them to the existing list.
     */
    suspend fun loadMore()

    /**
     * Fetches the details of a specific character based on the provided ID.
     *
     * @param id The unique identifier of the character to be fetched.
     * @return Details of the specified character.
     */
    suspend fun getCharacter(id: Int): Character
}