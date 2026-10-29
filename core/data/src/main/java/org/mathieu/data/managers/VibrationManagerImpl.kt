package org.mathieu.data.managers

import android.content.Context
import android.os.VibrationEffect
import org.mathieu.domain.managers.VibrationManager

/**
 * Fait vibrer l'appareil à travers le service système, obtenu par l'extension [vibrator].
 * Le [Context] arrive par Koin : c'est celui de l'application, qui vit aussi longtemps que ce Manager,
 * alors qu'un Context d'activité serait détruit à chaque rotation d'écran.
 * La permission `VIBRATE` est déclarée dans le Manifest de `app` ; normale, elle n'est jamais demandée à l'utilisateur.
 *
 * @param context Context de l'application, médiateur vers les services du téléphone.
 */
internal class VibrationManagerImpl(private val context: Context) : VibrationManager {

    override fun vibrate(durationMs: Long) {
        val vibrator = context.vibrator()
        // Certains appareils (tablettes, émulateurs) n'ont pas de vibreur : on ignore la demande plutôt que d'échouer.
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
    }

}
