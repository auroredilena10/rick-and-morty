package org.mathieu.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.mathieu.data.remote.responses.LocationResponse

/**
 * Le Service des lieux : il fait les appels HTTP et renvoie des DTO, qui collent au JSON de l'API.
 * Il ne connaît pas le Domain, et ce n'est pas lui qui choisit entre la base et l'API : c'est le repository.
 *
 * @param client client Ktor partagé, déjà configuré avec l'URL de l'API.
 */
internal class LocationApi(private val client: HttpClient) {

    /**
     * Demande un lieu à l'API (`GET location/{id}`).
     *
     * @param id identifiant du lieu.
     * @return le [LocationResponse] du lieu, avec les URL de ses résidents.
     * @throws HttpException si la requête échoue ou si le code n'est pas [HttpStatusCode.OK].
     */
    suspend fun getLocation(id: Int): LocationResponse? = client
        .get("location/$id")
        .accept(HttpStatusCode.OK)
        .body()

}
