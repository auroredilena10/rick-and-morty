package org.mathieu.domain.managers

/**
 * Ouvre une adresse dans une autre app du téléphone (le navigateur, en général).
 * Comme pour [VibrationManager], le ViewModel ne connaît que cette interface : c'est l'implémentation,
 * dans la couche Data, qui envoie l'Intent à Android.
 */
interface LinkManager {

    /**
     * Ouvre l'adresse dans l'app choisie par Android.
     * Si aucune app ne sait l'ouvrir, il ne fait rien, pas de crash.
     *
     * @param url adresse complète, par exemple l'image d'un personnage.
     */
    fun open(url: String)
}
