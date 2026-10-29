package org.mathieu.domain.models.location

/**
 * La version light d'une [Location] : juste de quoi présenter un lieu, sans ses résidents.
 * C'est ce qu'un personnage garde de son dernier lieu connu.
 *
 * Pourquoi une classe à part : une [Location] avec une liste de résidents vide voudrait dire
 * « personne n'habite ici », ce qui est faux. Et pas d'héritage, une `data class` ne peut pas en étendre une autre.
 *
 * @property id identifiant du lieu, le même que celui de la [Location] complète.
 * @property name nom du lieu.
 * @property type catégorie du lieu (planète, station spatiale…).
 * @property dimension dimension dans laquelle se trouve le lieu.
 */
data class LocationPreview(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
)
