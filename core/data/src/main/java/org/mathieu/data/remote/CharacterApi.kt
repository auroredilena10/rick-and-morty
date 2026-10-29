package org.mathieu.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import org.mathieu.data.remote.responses.CharacterResponse
import org.mathieu.data.remote.responses.PaginatedResponse

/**
 * Le Service des personnages : il fait les appels HTTP et renvoie des DTO, qui collent au JSON de l'API.
 * Il ne connaît pas le Domain, et ce n'est pas lui qui choisit entre la base et l'API : c'est le repository.
 *
 * @param client client Ktor partagé, déjà configuré avec l'URL de l'API.
 */
internal class CharacterApi(private val client: HttpClient) {

    /**
     * Fetches a list of characters from the API.
     *
     * If the page parameter is not provided, it defaults to fetching the first page.
     *
     * @param page The page number to fetch. If null, the first page is fetched by default.
     * @return A paginated response containing a list of [CharacterResponse] for the specified page.
     * @throws HttpException if the request fails or if the status code is not [HttpStatusCode.OK].
     */
    suspend fun getCharacters(page: Int?): PaginatedResponse<CharacterResponse> = client
        .get("character/") {
            if (page != null)
                url {
                    parameter("page", page)
                }
        }
        .accept(HttpStatusCode.OK)
        .body()

    /**
     * Fetches the details of a character with the given ID from the service.
     *
     * @param id The unique identifier of the character to retrieve.
     * @return The [CharacterResponse] representing the details of the character.
     * @throws HttpException if the request fails or if the status code is not [HttpStatusCode.OK].
     */
    suspend fun getCharacter(id: Int): CharacterResponse? = client
        .get("character/$id")
        .accept(HttpStatusCode.OK)
        .body()

    /**
     * Demande plusieurs personnages en un seul appel (`GET character/[1,2,3]`), au lieu d'un appel par résident.
     * Les crochets obligent l'API à répondre par une liste, même s'il n'y a qu'un seul id.
     *
     * @param ids identifiants des personnages voulus.
     * @return la liste des [CharacterResponse] trouvés.
     * @throws HttpException si la requête échoue ou si le code n'est pas [HttpStatusCode.OK].
     */
    suspend fun getCharacters(ids: List<Int>): List<CharacterResponse> = client
        .get("character/[${ids.joinToString(",")}]")
        .accept(HttpStatusCode.OK)
        .body()

}
