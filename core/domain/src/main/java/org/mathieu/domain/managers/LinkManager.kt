package org.mathieu.domain.managers

/**
 * Ouvre une adresse dans une autre application du téléphone (navigateur, galerie…).
 * L'interface vit dans le Domain pour que les ViewModels puissent ouvrir un lien sans connaître Android :
 * c'est l'implémentation, dans la couche Data, qui demande au système quelle application s'en charge.
 */
interface LinkManager {

    /**
     * Ouvre l'adresse dans l'application choisie par le système.
     * Ne fait rien si aucune application installée ne sait l'ouvrir.
     *
     * @param url adresse complète, par exemple celle de l'image d'un personnage.
     */
    fun open(url: String)
}
