package org.mathieu.domain.repositories

import org.mathieu.domain.models.location.Location
import org.mathieu.domain.models.location.LocationPreview

/**
 * Contrat pour obtenir les lieux de l'univers : la couche Data décide d'où ils viennent.
 * Deux formes existent pour qu'on ne charge les résidents d'un lieu que lorsqu'on en a besoin.
 */
interface LocationRepository {

    /**
     * Donne un lieu complet, avec tous les personnages qui y ont été vus en dernier.
     *
     * @param id identifiant unique du lieu.
     * @return le [Location] demandé, résidents compris.
     * @throws Exception si le lieu n'existe nulle part.
     */
    suspend fun getLocation(id: Int): Location

    /**
     * Donne la version légère d'un lieu, sans ses résidents.
     *
     * @param id identifiant unique du lieu.
     * @return le [LocationPreview] demandé.
     * @throws Exception si le lieu n'existe nulle part.
     */
    suspend fun getLocationPreview(id: Int): LocationPreview
}
