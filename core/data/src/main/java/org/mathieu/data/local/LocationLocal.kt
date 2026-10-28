package org.mathieu.data.local

import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import org.mathieu.data.local.objects.LocationObject

/**
 * Requêtes Realm sur les lieux : c'est le seul endroit de la couche Data qui connaît la table des lieux.
 *
 * @param database base Realm partagée de l'application.
 */
internal class LocationLocal(private val database: RealmDatabase) {

    /**
     * @param id identifiant unique du lieu.
     * @return le [LocationObject] enregistré, ou `null` s'il n'a jamais été chargé.
     */
    suspend fun getLocation(id: Int): LocationObject? = database.use {
        query<LocationObject>("id == $id").first().find()
    }

    /**
     * Enregistre le lieu, ou remplace la version déjà en base pour garder les données de l'API à jour.
     *
     * @param location lieu à enregistrer.
     */
    suspend fun insert(location: LocationObject) {
        database.write {
            copyToRealm(location, UpdatePolicy.ALL)
        }
    }

}
