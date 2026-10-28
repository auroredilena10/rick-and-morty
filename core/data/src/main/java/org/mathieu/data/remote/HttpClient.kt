package org.mathieu.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*

/**
 * Construit le client Ktor partagé par tous les services : URL de base, lecture du JSON, délais
 * et traduction des codes d'erreur sont réglés une fois ici au lieu d'être répétés dans chaque `XxxApi`.
 *
 * @param baseUrl adresse de l'API, ajoutée devant chaque chemin relatif (`character/1`).
 * @param engine moteur HTTP ; CIO par défaut, remplaçable par un moteur factice pour des tests.
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
                HttpStatusCode.Forbidden.value -> throw HttpException.ForbiddenAccess
                in 500..599 -> throw HttpException.InternalServerError
            }
        }

    }
}

/**
 * Envoie un corps en JSON en précisant son type, sans quoi le serveur ne saurait pas comment le lire.
 *
 * @receiver la requête en cours de construction.
 * @param body objet sérialisable à envoyer.
 */
inline fun <reified T> HttpRequestBuilder.setBodyJson(body: T) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

/**
 * Refuse toute réponse dont le code n'est pas attendu, pour qu'un 404 devienne une exception
 * au lieu d'un JSON d'erreur qu'on essaierait de lire comme une donnée.
 *
 * @receiver la réponse reçue.
 * @param codes codes HTTP acceptés.
 * @return la même réponse, si son code fait partie de [codes].
 * @throws HttpException.NotAccepted pour tout autre code.
 */
fun HttpResponse.accept(vararg codes: HttpStatusCode) = when (status) {
    in codes -> this
    else -> throw org.mathieu.data.remote.HttpException.NotAccepted
}

/**
 * Erreurs réseau connues. Une classe scellée liste tous les cas possibles,
 * pour qu'un `when` sur ces erreurs n'en oublie aucun.
 *
 * @property message description de l'erreur.
 */
sealed class HttpException(override val message: String): Exception() {
    object NotAccepted: HttpException(message = "Http code not accepted.")
    object ForbiddenAccess: HttpException(message = "Forbidden access.")
    object InternalServerError: HttpException(message = "Internal server error.")
}