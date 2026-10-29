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
 * Remplit le contrat [LocationRepository] : il cherche les lieux en base d'abord, et ne demande à l'API
 * que ce qui n'a jamais été enregistré.
 *
 * Choix : les résidents passent par [CharacterApi] et [CharacterLocal], pas par `CharacterRepository`.
 * `CharacterRepositoryImpl` a déjà besoin de [LocationRepository] pour remplir `Character.location`,
 * et Koin ne sait pas créer deux classes qui ont besoin l'une de l'autre.
 *
 * @param locationApi Service des lieux.
 * @param locationLocal DAO des lieux.
 * @param characterApi Service des personnages, pour les résidents pas encore en base.
 * @param characterLocal DAO des personnages.
 */
internal class LocationRepositoryImpl(
    private val locationApi: LocationApi,
    private val locationLocal: LocationLocal,
    private val characterApi: CharacterApi,
    private val characterLocal: CharacterLocal
) : LocationRepository {

    /**
     * Donne le lieu complet. Pour chaque résident, son dernier lieu connu est ce lieu-ci : c'est la définition
     * d'un résident dans l'API (un personnage vu ici en dernier). Ça évite de recharger le même lieu pour chacun.
     *
     * @param id identifiant du lieu.
     * @return la [Location] avec ses résidents.
     * @throws Exception si le lieu n'est ni en base ni sur l'API.
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
     * Le fetch « local d'abord » d'un lieu :
     * 1. On cherche en base.
     * 2. Pas trouvé : on demande à l'API.
     * 3. L'API le renvoie : on l'enregistre en base, puis on le rend.
     * 4. Toujours rien : exception.
     *
     * Les deux fonctions publiques passent par ici, donc la logique n'est écrite qu'une fois.
     *
     * @param id identifiant du lieu.
     * @return le [LocationObject] en base.
     * @throws Exception si le lieu n'est ni en base ni sur l'API.
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
     * Le même principe pour les résidents, mais en lot : ceux déjà en base d'abord, puis tous les autres
     * en un seul appel à l'API. On les enregistre pour que la prochaine ouverture du lieu se passe du réseau.
     *
     * @param ids identifiants des résidents du lieu.
     * @return les [CharacterObject] des résidents, triés par id pour avoir toujours le même ordre,
     * qu'ils viennent de la base ou de l'API.
     */
    private suspend fun getResidents(ids: List<Int>): List<CharacterObject> {
        val saved = characterLocal.getCharacters(ids)
        // Les id qui ne sont pas encore en base : ce sont les seuls qu'on demande à l'API.
        val missingIds = ids - saved.map { it.id }.toSet()

        val fetched = if (missingIds.isEmpty()) emptyList()
        else characterApi.getCharacters(missingIds).map(transform = CharacterResponse::toRealmObject)

        characterLocal.saveCharacters(fetched)

        return (saved + fetched).sortedBy { it.id }
    }

}
