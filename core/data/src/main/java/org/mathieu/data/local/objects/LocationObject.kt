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
 * Lieu enregistré dans Realm. Les résidents ne sont gardés que par identifiant : les personnages
 * ont déjà leur propre table, les recopier ici créerait deux versions d'une même donnée.
 *
 * @property id identifiant unique du lieu, le même que celui de l'API.
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
 * Transforme la réponse de l'API en objet Realm prêt à être inséré.
 * L'API ne donne que l'URL de chaque résident : on n'en garde que l'identifiant.
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
 * @receiver le lieu lu en base.
 * @return la version légère du lieu, sans ses résidents.
 */
internal fun LocationObject.toPreview() = LocationPreview(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)

/**
 * @receiver le lieu lu en base.
 * @param residents personnages déjà chargés à partir de [LocationObject.residentIds].
 * @return le [Location] complet.
 */
internal fun LocationObject.toModel(residents: List<Character>) = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residents = residents
)
