package org.mathieu.data.di

import io.ktor.client.HttpClient
import org.koin.dsl.module
import org.mathieu.data.repositories.CharacterRepositoryImpl
import org.mathieu.data.repositories.LocationRepositoryImpl
import org.mathieu.domain.repositories.CharacterRepository
import org.mathieu.domain.repositories.LocationRepository

//https://rickandmortyapi.com/documentation/#rest
private const val RMAPI_URL = "https://rickandmortyapi.com/api/"

val dataModule = module {

    single<HttpClient> {
        org.mathieu.data.remote.createHttpClient(
            baseUrl = RMAPI_URL
        )
    }

    single<org.mathieu.data.local.RealmDatabase> { org.mathieu.data.local.RMDatabase() }

    single { org.mathieu.data.local.CharacterLocal(get()) }

    single { org.mathieu.data.remote.CharacterApi(get()) }

    // Chaque repository est déclaré sous son interface du Domain : les ViewModels demandent `CharacterRepository`
    // et ne voient jamais l'implémentation, qui reste `internal` à ce module.
    single<CharacterRepository> {
        CharacterRepositoryImpl(
            get(),
            get(),
            get(),
            get()
        )
    }

    single { org.mathieu.data.local.LocationLocal(get()) }

    single { org.mathieu.data.remote.LocationApi(get()) }

    // Dépendance à sens unique : CharacterRepositoryImpl reçoit LocationRepository, jamais l'inverse,
    // sinon Koin tournerait en rond pour construire l'un ou l'autre.
    single<LocationRepository> {
        LocationRepositoryImpl(
            get(),
            get(),
            get(),
            get()
        )
    }

}