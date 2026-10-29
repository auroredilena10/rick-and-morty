package org.mathieu.data.local

import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mathieu.data.local.objects.CharacterObject

/**
 * Le DAO des personnages : il fait les requêtes Realm sur leur table, et personne d'autre n'y touche.
 *
 * @param database base Realm partagée de l'application.
 */
internal class CharacterLocal(private val database: RealmDatabase) {

    /**
     * Observe la table au lieu de la lire une seule fois : à chaque page enregistrée, la liste est
     * renvoyée et l'écran se met à jour tout seul.
     *
     * @return un [Flow] qui renvoie tous les personnages en base, à chaque changement de la table.
     */
    suspend fun getCharacters(): Flow<List<CharacterObject>> = database.use {
        query<CharacterObject>().find().asFlow().map { it.list }
    }

    /**
     * @param id identifiant du personnage.
     * @return le [CharacterObject] en base, ou `null` s'il n'a jamais été chargé.
     */
    suspend fun getCharacter(id: Int): CharacterObject? = database.use {
        query<CharacterObject>("id == $id").first().find()
    }

    /**
     * Lit plusieurs personnages en une seule requête au lieu d'une par id :
     * la Citadelle a plus de cent résidents.
     *
     * @param ids identifiants des personnages voulus.
     * @return les [CharacterObject] déjà en base parmi [ids] ; ceux jamais chargés ne sont pas dans la liste.
     */
    suspend fun getCharacters(ids: List<Int>): List<CharacterObject> =
        if (ids.isEmpty()) emptyList()
        // Donne par exemple "id IN {1, 2, 3}", la façon d'écrire une liste dans une requête Realm.
        // Une liste vide ferait planter la requête, d'où le test juste au-dessus.
        else database.use { query<CharacterObject>("id IN {${ids.joinToString()}}").find() }

    /**
     * Enregistre tous les personnages dans une seule transaction. Avant, il y en avait une par personnage,
     * et l'écran d'un lieu avec beaucoup de résidents mettait plusieurs secondes à s'afficher.
     *
     * @param characters personnages à enregistrer, par exemple une page entière de l'API.
     */
    suspend fun saveCharacters(characters: List<CharacterObject>) {
        if (characters.isEmpty()) return
        database.write {
            characters.forEach { copyToRealm(it, UpdatePolicy.ALL) }
        }
    }

    /**
     * Enregistre le personnage. S'il existe déjà, il est remplacé (UpdatePolicy.ALL), pour garder la version de l'API.
     *
     * @param character personnage à enregistrer.
     */
    suspend fun insert(character: CharacterObject) {
        database.write {
            copyToRealm(character, UpdatePolicy.ALL)
        }
    }

}