package org.mathieu.data.local.objects

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mathieu.data.remote.responses.CharacterResponse
import org.mathieu.data.remote.responses.idFromUrl
import org.mathieu.data.repositories.tryOrNull
import org.mathieu.domain.models.character.*
import org.mathieu.domain.models.location.LocationPreview

/**
 * Represents a character entity stored in the SQLite database. This object provides fields
 * necessary to represent all the attributes of a character from the data source.
 * The object is specifically tailored for SQLite storage using Realm.
 *
 * @property id Unique identifier of the character.
 * @property name Name of the character.
 * @property status Current status of the character (e.g. 'Alive', 'Dead').
 * @property species Biological species of the character.
 * @property type The type or subspecies of the character.
 * @property gender Gender of the character (e.g. 'Female', 'Male').
 * @property originName The origin location name.
 * @property originId The origin location id.
 * @property locationName The current location name.
 * @property locationId The current location id.
 * @property image URL pointing to the character's avatar image.
 * @property created Timestamp indicating when the character entity was created in the database.
 */
internal class CharacterObject: RealmObject {
    @PrimaryKey
    var id: Int = -1
    var name: String = ""
    var status: String = ""
    var species: String = ""
    var type: String = ""
    var gender: String = ""
    var originName: String = ""
    var originId: Int = -1
    var locationName: String = ""
    var locationId: Int = -1
    var image: String = ""
    var created: String = ""
}


/**
 * Transforme la réponse de l'API en objet Realm prêt à être inséré.
 * Les lieux ne sont gardés que par nom et identifiant : leur détail vit dans la table des lieux.
 *
 * @receiver la réponse reçue pour un personnage.
 * @return le [CharacterObject] correspondant.
 */
internal fun CharacterResponse.toRealmObject() = CharacterObject().also { obj ->
    obj.id = id
    obj.name = name
    obj.status = status
    obj.species = species
    obj.type = type
    obj.gender = gender
    obj.originName = origin.name
    obj.originId = origin.url.idFromUrl() ?: -1
    obj.locationName = location.name
    obj.locationId = location.url.idFromUrl() ?: -1
    obj.image = image
    obj.created = created
}

/**
 * @receiver le personnage lu en base.
 * @param location dernier lieu connu, chargé à part car la table des personnages n'en garde que l'identifiant.
 * @return le [Character] complet.
 */
internal fun CharacterObject.toModel(location: LocationPreview?) = Character(
    id = id,
    name = name,
    status = tryOrNull { CharacterStatus.valueOf(status) } ?: CharacterStatus.Unknown,
    species = species,
    type = type,
    gender = tryOrNull { CharacterGender.valueOf(gender) } ?: CharacterGender.Unknown,
    origin = originName to originId,
    location = location,
    avatarUrl = image
)
