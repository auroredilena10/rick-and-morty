package org.mathieu.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*

/**
 * Crée le client Ktor utilisé par tous les Services. L'adresse de l'API, la lecture du JSON, les délais
 * et les erreurs HTTP sont réglés une fois ici, au lieu d'être répétés dans chaque `XxxApi`.
 *
 * @param baseUrl adresse de l'API, ajoutée devant chaque chemin (`character/1`).
 * @param engine moteur HTTP, CIO par défaut. On peut en passer un faux pour des tests.
 * @return le [HttpClient] prêt à l'emploi.
 */
fun createHttpClient(
    baseUrl: String,
    engine: HttpClientEngine = CIO.create()
): HttpClient = HttpClient(engine) {
    defaultRequest { url(baseUrl) }


    install(ContentNegotiation) {
        json()
    }

    install(HttpTimeout) {
        connectTimeoutMillis = 15000
        socketTimeoutMillis = 15000
        requestTimeoutMillis = 15000
    }

    install(HttpCallValidator) {
        validateResponse {
            when (it.status.value) {
                HttpStatusCode.Forbidden.value -> throw HttpException.ForbiddenAccess()
                in 500..599 -> throw HttpException.InternalServerError()
            }
        }

    }
}

/**
 * Refuse une réponse dont le code n'est pas celui attendu. Sans ça, un 404 renverrait un JSON d'erreur
 * qu'on essaierait de lire comme un personnage.
 *
 * @receiver la réponse reçue.
 * @param codes codes HTTP acceptés.
 * @return la même réponse, si son code fait partie de [codes].
 * @throws HttpException.NotAccepted pour tout autre code.
 */
fun HttpResponse.accept(vararg codes: HttpStatusCode) = when (status) {
    in codes -> this
    else -> throw HttpException.NotAccepted()
}

/**
 * Les erreurs réseau qu'on connaît. `sealed` liste tous les cas, donc un `when` dessus n'en oublie aucun.
 * Ce sont des `class` et pas des `object` : une exception retient l'endroit où elle a été lancée,
 * il en faut une nouvelle à chaque fois.
 *
 * @param message description de l'erreur, gardée par [Exception] et lisible avec `message`.
 */
sealed class HttpException(message: String) : Exception(message) {
    class NotAccepted : HttpException(message = "Http code not accepted.")
    class ForbiddenAccess : HttpException(message = "Forbidden access.")
    class InternalServerError : HttpException(message = "Internal server error.")
}