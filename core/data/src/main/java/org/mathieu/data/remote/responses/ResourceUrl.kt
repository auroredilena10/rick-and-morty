package org.mathieu.data.remote.responses

/**
 * Extrait l'identifiant d'une ressource à partir de son URL : l'API ne relie les personnages et les lieux
 * que par des URL du type `https://rickandmortyapi.com/api/location/3`.
 * Écrit une seule fois pour que tous les mappers suivent la même règle.
 *
 * @receiver URL d'un personnage ou d'un lieu, telle que l'API la renvoie.
 * @return l'identifiant, ou `null` si l'URL est vide (lieu « unknown ») ou ne finit pas par un nombre.
 */
internal fun String.idFromUrl(): Int? = substringAfterLast("/").toIntOrNull()
