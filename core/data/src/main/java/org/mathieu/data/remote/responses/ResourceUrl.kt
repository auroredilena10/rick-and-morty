package org.mathieu.data.remote.responses

/**
 * Récupère l'id d'un personnage ou d'un lieu à partir de son URL. L'API ne relie les personnages et les lieux
 * que par des URL comme `https://rickandmortyapi.com/api/location/3` : l'id, c'est ce qui suit le dernier `/`.
 * Elle est écrite une seule fois pour que tous les mappers fassent pareil.
 *
 * @receiver l'URL telle que l'API la renvoie.
 * @return l'id, ou `null` si l'URL est vide (lieu « unknown ») ou ne finit pas par un nombre.
 */
internal fun String.idFromUrl(): Int? = substringAfterLast("/").toIntOrNull()
