package org.mathieu.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.mathieu.data.remote.responses.LocationResponse

/**
 * Service distant des lieux : il ne fait que l'appel HTTP et rend la forme brute du JSON ;
 * le choix entre base et réseau appartient au repository.
 *
 * @param client client Ktor partagé, déjà configuré avec l'URL de l'API.
 */
internal class LocationApi(private val client: HttpClient) {

    /**
     * Demande au service les détails d'un lieu.
     *
     * @param id identifiant unique du lieu.
     * @return le [LocationResponse] du lieu, avec les URL de ses résidents.
     * @throws HttpException si la requête échoue ou si le code n'est pas [HttpStatusCode.OK].
     */
    suspend fun getLocation(id: Int): LocationResponse? = client
        .get("location/$id")
        .accept(HttpStatusCode.OK)
        .body()

}
