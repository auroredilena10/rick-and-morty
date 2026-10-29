package org.mathieu.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Renvoie `null` au lieu de planter. Sert dans les mappers, quand une valeur inconnue a déjà une valeur
 * de secours (par exemple un statut que l'enum ne connaît pas devient `Unknown`).
 *
 * @param block calcul qui peut échouer.
 * @return le résultat de [block], ou `null` s'il a levé une exception.
 */
fun <T> tryOrNull(block: () -> T) = try {
    block()
} catch (_: Exception) {
    null
}

/**
 * Mappe chaque élément de chaque liste d'un [Flow]. Ça évite d'écrire deux `map` l'un dans l'autre
 * à chaque fois qu'on transforme un flux d'objets Realm en modèles.
 *
 * @receiver le flux de listes à transformer.
 * @param transform la conversion d'un élément, qui peut être `suspend` (par exemple pour charger un lieu).
 * @return un flux qui renvoie les listes transformées.
 */
inline fun <T, R> Flow<List<T>>.mapElement(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    this.map { list ->
        list.map { element -> transform(element) }
    }
