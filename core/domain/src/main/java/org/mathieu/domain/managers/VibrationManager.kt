package org.mathieu.domain.managers

/**
 * Le technicien du vibreur. Le ViewModel lui dit juste « vibre », sans savoir comment marche un vibreur.
 * L'interface est dans le Domain pour ne rien connaître d'Android, l'implémentation est dans la couche Data.
 */
interface VibrationManager {

    /**
     * Fait vibrer une fois, pour confirmer un clic.
     * Pas de vibreur ou pas de permission : il ne fait rien, pas de crash.
     *
     * @param durationMs durée de la vibration, en millisecondes.
     */
    fun vibrate(durationMs: Long)
}
