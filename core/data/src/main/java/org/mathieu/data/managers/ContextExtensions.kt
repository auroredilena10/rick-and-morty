package org.mathieu.data.managers

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager

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
