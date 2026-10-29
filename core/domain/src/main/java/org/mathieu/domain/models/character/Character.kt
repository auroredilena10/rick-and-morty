package org.mathieu.domain.models.character

import org.mathieu.domain.models.location.LocationPreview

/**
 * Un personnage de la série, tel que l'app le manipule. Il ne sait pas où il est stocké ni comment on l'affiche.
 *
 * Les choix faits ici :
 * - `location` est un [LocationPreview], pas une [org.mathieu.domain.models.location.Location] : le personnage
 *   n'a besoin que du nom et du type de son lieu. La version complète chargerait aussi tous les résidents.
 * - `location` peut être `null` : l'API répond « unknown » pour certains personnages. Un faux lieu vide
 *   obligerait chaque écran à le repérer.
 * - `origin` reste un couple nom / id : aucun écran ne l'affiche en détail, et en faire un [LocationPreview]
 *   coûterait un appel de plus par personnage. Si un jour il le faut, on change son type comme pour `location`.
 *
 * @property id The unique identifier for the character.
 * @property name The name of the character.
 * @property status The current status of the character (Alive, Dead, or Unknown).
 * @property species The species or classification of the character.
 * @property type Further description or subspecies of the character.
 * @property gender The gender of the character.
 * @property origin The origin location of the character, represented as a name and an id of location.
 * @property location dernier lieu connu du personnage, `null` s'il est inconnu.
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
