package org.mathieu.data.di

import io.ktor.client.HttpClient
import org.koin.dsl.module
import org.mathieu.data.managers.LinkManagerImpl
import org.mathieu.data.managers.VibrationManagerImpl
import org.mathieu.data.repositories.CharacterRepositoryImpl
import org.mathieu.data.repositories.LocationRepositoryImpl
import org.mathieu.domain.managers.LinkManager
import org.mathieu.domain.managers.VibrationManager
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

    // On déclare le repository avec le type de son interface : quand un ViewModel demande `CharacterRepository`,
    // Koin lui donne l'implémentation, que le ViewModel ne voit jamais (elle est `internal`).
    // Chaque get() est rempli par Koin avec ce qui est déclaré plus haut (Context, Api, Local...).
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

    // CharacterRepositoryImpl reçoit LocationRepository, mais jamais l'inverse :
    // si chacun avait besoin de l'autre, Koin tournerait en rond pour les créer.
    single<LocationRepository> {
        LocationRepositoryImpl(
            get(),
            get(),
            get(),
            get()
        )
    }

    // Les Managers aussi sont déclarés avec leur interface. Ici le get() donne le Context de l'application,
    // celui enregistré par androidContext() au démarrage de Koin (CleanRmApiApplication).
    single<VibrationManager> { VibrationManagerImpl(get()) }

    single<LinkManager> { LinkManagerImpl(get()) }

}