package org.mathieu.domain.managers

/**
 * Point d'accès unique au vibreur de l'appareil. L'interface vit dans le Domain pour que les ViewModels
 * puissent faire vibrer sans connaître Android : l'implémentation, qui parle au matériel, est dans la couche Data.
 */
interface VibrationManager {

    /**
     * Fait vibrer l'appareil une fois, pour confirmer une action de l'utilisateur.
     * Ne fait rien si l'appareil n'a pas de vibreur.
     *
     * @param durationMs durée de la vibration, en millisecondes.
     */
    fun vibrate(durationMs: Long)
}
