package org.mathieu.domain.models.character

import org.mathieu.domain.models.location.LocationPreview

/**
 * Personnage de l'univers, tel que l'application le manipule : il ne sait ni où il est rangé ni comment il est affiché.
 *
 * Choix de modélisation :
 * - `location` est un [LocationPreview] et pas une [org.mathieu.domain.models.location.Location] : un personnage
 *   n'a besoin que de présenter son lieu, et la forme complète chargerait tous les résidents, eux-mêmes des personnages.
 * - `location` peut être `null`, car certains personnages n'ont pas de dernier lieu connu ; un faux lieu « vide »
 *   obligerait chaque écran à le reconnaître.
 * - `origin` reste un couple nom / identifiant : rien ne l'affiche en détail aujourd'hui, et un [LocationPreview]
 *   demanderait de charger un lieu de plus par personnage. Le jour où il le faudra, il suffira de changer son type
 *   comme pour `location`.
 *
 * @property id The unique identifier for the character.
 * @property name The name of the character.
 * @property status The current status of the character (Alive, Dead, or Unknown).
 * @property species The species or classification of the character.
 * @property type Further description or subspecies of the character.
 * @property gender The gender of the character.
 * @property origin The origin location of the character, represented as a name and an id of location.
 * @property location dernier lieu connu du personnage, ou `null` quand ce lieu est inconnu.
 * @property avatarUrl A URL pointing to an avatar or image of the character.
 */
data class Character(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val type: String,
    val gender: CharacterGender,
    val origin: Pair<String, Int>,
    val location: LocationPreview?,
    val avatarUrl: String
)

/**
 * Describes the current state or condition of a character.
 */
enum class CharacterStatus {
    Alive, Dead, Unknown
}

/**
 * Represents the gender classification of a character.
 */
enum class CharacterGender {
    Female, Male, Genderless, Unknown
}
