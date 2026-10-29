package org.mathieu.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Transforme un échec en `null`, pour les conversions où une valeur inconnue a déjà un repli
 * (par exemple un statut que l'enum ne connaît pas devient `Unknown`).
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
 * Applique [transform] à chaque élément de chaque liste émise, pour mapper un flux d'objets Realm
 * en flux de modèles sans écrire deux `map` imbriqués à chaque fois.
 *
 * @receiver flux de listes à transformer.
 * @param transform conversion d'un élément, qui peut suspendre (par exemple pour charger un lieu).
 * @return un flux qui émet les listes transformées.
 */
inline fun <T, R> Flow<List<T>>.mapElement(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    this.map { list ->
        list.map { element -> transform(element) }
    }
