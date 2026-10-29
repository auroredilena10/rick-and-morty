package org.mathieu.data.local

import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import org.mathieu.data.local.objects.LocationObject

/**
 * Le DAO des lieux : il fait les requêtes Realm sur la table des lieux, et personne d'autre n'y touche.
 *
 * @param database base Realm partagée de l'application.
 */
internal class LocationLocal(private val database: RealmDatabase) {

    /**
     * @param id identifiant du lieu.
     * @return le [LocationObject] en base, ou `null` s'il n'a jamais été chargé.
     */
    suspend fun getLocation(id: Int): LocationObject? = database.use {
        query<LocationObject>("id == $id").first().find()
    }

    /**
     * Enregistre le lieu. S'il existe déjà, il est remplacé (UpdatePolicy.ALL), pour garder la version de l'API.
     *
     * @param location lieu à enregistrer.
     */
    suspend fun insert(location: LocationObject) {
        database.write {
            copyToRealm(location, UpdatePolicy.ALL)
        }
    }

}
