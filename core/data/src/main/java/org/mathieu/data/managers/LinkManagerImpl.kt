package org.mathieu.data.managers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import org.mathieu.domain.managers.LinkManager

/**
 * Ouvre une adresse avec un Intent implicite : on ne dit pas *quelle* app doit l'ouvrir, seulement *quoi faire*
 * (`ACTION_VIEW` sur une URL). Android choisit l'app installée qui sait le faire, en général le navigateur.
 *
 * @param context Context de l'application, donné par Koin, qui permet de lancer une autre app.
 */
internal class LinkManagerImpl(private val context: Context) : LinkManager {

    override fun open(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            // Obligatoire avec le Context de l'application : il n'a pas de pile d'écrans, alors l'app ouverte en crée une.
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // Aucune app ne sait ouvrir l'adresse : on ignore le clic, pas de crash.
        }
    }

}
