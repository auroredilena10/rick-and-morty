package org.mathieu.data.managers

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat

/**
 * Dit si l'application a le droit d'utiliser une permission, pour qu'un Manager vérifie lui-même
 * avant de toucher au matériel au lieu de planter.
 *
 * @receiver le Context qui interroge le système ; celui de l'application suffit.
 * @param permission la permission à vérifier, par exemple `Manifest.permission.VIBRATE`.
 * @return `true` si la permission est accordée.
 * @see ContextCompat.checkSelfPermission
 */
internal fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

/**
 * Donne le vibreur principal de l'appareil, en cachant le changement d'API d'Android 12 :
 * depuis, les vibreurs passent par [VibratorManager], qui peut en gérer plusieurs.
 * Le projet visant Android 9 au minimum, les deux chemins restent nécessaires.
 *
 * @receiver n'importe quel [Context] ; celui de l'application suffit, le service ne dépend d'aucun écran.
 * @return le [Vibrator] par défaut de l'appareil.
 */
internal fun Context.vibrator(): Vibrator =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        getSystemService(VibratorManager::class.java).defaultVibrator
    else
        getSystemService(Vibrator::class.java)
