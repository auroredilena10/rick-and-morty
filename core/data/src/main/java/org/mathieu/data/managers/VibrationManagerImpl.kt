package org.mathieu.data.managers

import android.Manifest
import android.annotation.SuppressLint
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

    // Lint ne voit pas la vérification faite dans l'extension hasPermission ci-dessous : sans cette annotation,
    // il croirait que la permission VIBRATE n'est jamais contrôlée.
    @SuppressLint("MissingPermission")
    override fun vibrate(durationMs: Long) {
        // Le Manager vérifie lui-même : sans la permission, on ne vibre pas plutôt que de faire planter l'app.
        if (!context.hasPermission(Manifest.permission.VIBRATE)) return
        val vibrator = context.vibrator()
        // Certains appareils (tablettes, émulateurs) n'ont pas de vibreur : on ignore la demande plutôt que d'échouer.
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
    }

}
