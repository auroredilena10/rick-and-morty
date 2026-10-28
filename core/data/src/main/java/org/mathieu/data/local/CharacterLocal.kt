package org.mathieu.data.local

import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mathieu.data.local.objects.CharacterObject

/**
 * Requêtes Realm sur les personnages : c'est le seul endroit de la couche Data qui connaît leur table.
 *
 * @param database base Realm partagée de l'application.
 */
internal class CharacterLocal(private val database: RealmDatabase) {

    /**
     * Observe la table plutôt que de la lire une fois : chaque page insérée par la pagination
     * réémet la liste, et l'écran se met à jour sans redemander.
     *
     * @return un [Flow] qui émet tous les personnages enregistrés, à chaque changement de la table.
     */
    suspend fun getCharacters(): Flow<List<CharacterObject>> = database.use {
        query<CharacterObject>().find().asFlow().map { it.list }
    }

    /**
     * @param id identifiant unique du personnage.
     * @return le [CharacterObject] enregistré, ou `null` s'il n'a jamais été chargé.
     */
    suspend fun getCharacter(id: Int): CharacterObject? = database.use {
        query<CharacterObject>("id == $id").first().find()
    }

    /**
     * @param characters personnages à enregistrer, par exemple une page entière de l'API.
     * @return la même liste, pour pouvoir enchaîner.
     */
    suspend fun saveCharacters(characters: List<CharacterObject>) = characters.onEach {
        insert(it)
    }

    /**
     * Enregistre le personnage, ou remplace la version déjà en base pour garder les données de l'API à jour.
     *
     * @param character personnage à enregistrer.
     */
    suspend fun insert(character: CharacterObject) {
        database.write {
            copyToRealm(character, UpdatePolicy.ALL)
        }
    }

}