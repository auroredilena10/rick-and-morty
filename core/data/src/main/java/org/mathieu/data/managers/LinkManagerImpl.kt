package org.mathieu.data.managers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import org.mathieu.domain.managers.LinkManager

/**
 * Ouvre une adresse avec un `Intent` implicite : l'application ne dit pas *qui* doit l'ouvrir, seulement *quoi*
 * (`ACTION_VIEW` sur une URL), et Android choisit l'application installée qui sait le faire.
 *
 * @param context Context de l'application, injecté par Koin, qui permet de lancer une autre application.
 */
internal class LinkManagerImpl(private val context: Context) : LinkManager {

    override fun open(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            // Obligatoire : le Context de l'application n'a pas de pile d'écrans, l'application ouverte en démarre une.
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // Aucune application ne sait ouvrir l'adresse : on ignore le clic plutôt que de faire planter l'app.
        }
    }

}
