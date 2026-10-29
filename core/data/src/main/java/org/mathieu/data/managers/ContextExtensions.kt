package org.mathieu.data.managers

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat

/**
 * Dit si l'app a une permission. Le Manager vérifie lui-même avant de toucher au matériel :
 * si la permission manque, il ne fait rien au lieu de planter.
 *
 * @receiver le Context qui interroge le système ; celui de l'application suffit.
 * @param permission la permission à vérifier, par exemple `Manifest.permission.VIBRATE`.
 * @return `true` si la permission est accordée.
 * @see ContextCompat.checkSelfPermission
 */
internal fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

/**
 * Donne le vibreur du téléphone. Depuis Android 12, on passe par [VibratorManager] (un téléphone peut avoir
 * plusieurs vibreurs) ; avant, on demandait directement le [Vibrator]. L'app tourne dès Android 9,
 * donc il faut garder les deux chemins : le `if` choisit selon la version du téléphone.
 *
 * @receiver n'importe quel [Context] ; celui de l'application suffit, le vibreur ne dépend d'aucun écran.
 * @return le [Vibrator] par défaut de l'appareil.
 */
internal fun Context.vibrator(): Vibrator =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        getSystemService(VibratorManager::class.java).defaultVibrator
    else
        getSystemService(Vibrator::class.java)
