package org.mathieu.data.local.objects

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mathieu.data.remote.responses.LocationResponse
import org.mathieu.data.remote.responses.idFromUrl
import org.mathieu.domain.models.character.Character
import org.mathieu.domain.models.location.Location
import org.mathieu.domain.models.location.LocationPreview

/**
 * L'Entity d'un lieu : la forme d'une ligne de la table des lieux dans Realm.
 * On ne garde que les id des résidents : les personnages ont déjà leur table, les recopier ici
 * ferait deux versions de la même donnée.
 *
 * @property id identifiant du lieu, le même que sur l'API.
 * @property name nom du lieu.
 * @property type catégorie du lieu.
 * @property dimension dimension dans laquelle se trouve le lieu.
 * @property residentIds identifiants des personnages vus en dernier dans ce lieu.
 */
internal class LocationObject : RealmObject {
    @PrimaryKey
    var id: Int = -1
    var name: String = ""
    var type: String = ""
    var dimension: String = ""
    var residentIds: RealmList<Int> = realmListOf()
}


/**
 * Mapper DTO → Entity : transforme la réponse de l'API en objet Realm, prêt à être inséré.
 * L'API donne une URL par résident, on n'en garde que l'id.
 *
 * @receiver la réponse reçue pour un lieu.
 * @return le [LocationObject] correspondant.
 */
internal fun LocationResponse.toRealmObject() = LocationObject().also { obj ->
    obj.id = id
    obj.name = name
    obj.type = type
    obj.dimension = dimension
    obj.residentIds = residents
        .mapNotNull { url -> url.idFromUrl() }
        .toRealmList()
}

/**
 * Mapper Entity → Domain, version light.
 *
 * @receiver le lieu lu en base.
 * @return le [LocationPreview] du lieu, sans ses résidents.
 */
internal fun LocationObject.toPreview() = LocationPreview(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)

/**
 * Mapper Entity → Domain, version complète. Les résidents arrivent déjà chargés,
 * parce que l'objet Realm ne connaît que leurs id.
 *
 * @receiver le lieu lu en base.
 * @param residents personnages chargés à partir de [LocationObject.residentIds].
 * @return la [Location] complète.
 */
internal fun LocationObject.toModel(residents: List<Character>) = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residents = residents
)
