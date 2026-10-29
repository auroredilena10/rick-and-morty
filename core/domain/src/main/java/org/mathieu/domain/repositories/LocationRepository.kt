package org.mathieu.domain.repositories

import org.mathieu.domain.models.location.Location
import org.mathieu.domain.models.location.LocationPreview

/**
 * Le contrat pour récupérer les lieux. Le Domain dit ce qu'il veut, c'est la couche Data qui décide
 * d'où ça vient (base locale ou API). Il y a deux fonctions pour ne charger les résidents que quand on en a besoin.
 */
interface LocationRepository {

    /**
     * Donne un lieu complet, avec les personnages qui y ont été vus en dernier.
     *
     * @param id identifiant du lieu.
     * @return la [Location] demandée, résidents compris.
     * @throws Exception si le lieu n'est ni en base ni sur l'API.
     */
    suspend fun getLocation(id: Int): Location

    /**
     * Donne la version light d'un lieu, sans ses résidents.
     *
     * @param id identifiant du lieu.
     * @return le [LocationPreview] demandé.
     * @throws Exception si le lieu n'est ni en base ni sur l'API.
     */
    suspend fun getLocationPreview(id: Int): LocationPreview
}
