package org.mathieu.domain.models.location

/**
 * Version légère d'une [Location] : ce qu'il faut pour présenter un lieu sans charger ses résidents,
 * par exemple le dernier lieu connu d'un personnage.
 *
 * Classe séparée plutôt qu'une [Location] aux résidents vides : une liste vide voudrait dire « personne n'habite ici »,
 * ce qui serait faux. Pas d'héritage non plus entre les deux : ce sont deux vues d'un même lieu, et une `data class`
 * ne peut pas en étendre une autre.
 *
 * @property id identifiant unique du lieu, le même que celui de la [Location] complète.
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
