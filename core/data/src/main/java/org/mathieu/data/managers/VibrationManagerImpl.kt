package org.mathieu.data.managers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import org.mathieu.domain.managers.VibrationManager

/**
 * Le technicien du vibreur : c'est lui qui parle au matériel, à travers le service récupéré par [vibrator].
 * Il reçoit le [Context] de l'application par Koin. Ce Context vit aussi longtemps que le Manager,
 * alors que celui d'une Activity est détruit à chaque rotation d'écran.
 * `VIBRATE` est une permission normale : déclarée dans le Manifest de `app`, sans popup pour l'utilisateur.
 *
 * @param context Context de l'application, qui donne accès aux services du téléphone.
 */
internal class VibrationManagerImpl(private val context: Context) : VibrationManager {

    // Lint ne voit pas la vérification qui est dans l'extension hasPermission (juste en dessous),
    // il croit donc que VIBRATE n'est jamais vérifiée. La permission est bien contrôlée, d'où le Suppress.
    @SuppressLint("MissingPermission")
    override fun vibrate(durationMs: Long) {
        // Le Manager vérifie lui-même : sans la permission, il ne vibre pas, pas de crash.
        if (!context.hasPermission(Manifest.permission.VIBRATE)) return
        val vibrator = context.vibrator()
        // Certains appareils (tablettes, émulateurs) n'ont pas de vibreur : on ne fait rien.
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
    }

}
