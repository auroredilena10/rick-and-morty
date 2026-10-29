@file:Suppress("UnusedReceiverParameter")

package org.mathieu.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

/**
 * Base de tous les ViewModels d'écran : elle porte le flux MVI à sens unique.
 * L'écran lit [state] (ce qu'il affiche) et [events] (ce qu'il doit faire une seule fois, comme naviguer) ;
 * le ViewModel ne les modifie qu'à travers [updateState] et [sendEvent].
 * Hérite de `KoinComponent` pour que chaque ViewModel reçoive ses repositories et managers par `by inject()`.
 *
 * @param State type de l'état propre à l'écran, une `data class` immuable.
 * @param initialState état affiché avant tout chargement.
 * @param application nécessaire à `AndroidViewModel`, fournie par `viewModel()`.
 * @property state état courant, observé par l'écran.
 * @property events événements ponctuels ; un canal plutôt qu'un état, pour qu'une navigation ne soit pas rejouée
 * quand l'écran se recompose.
 */
open class ViewModel<State>(initialState: State, application: Application): AndroidViewModel(application),
    KoinComponent {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State>
        get() = _state

    private val _events = Channel<Any>(Channel.BUFFERED)
    val events: Flow<Any>
        get() = _events.receiveAsFlow()

    /** This function is made as an extension because when we call it, it is yellow
     * and this is better for our UX
     *
     * @param block calcule le nouvel état à partir de l'actuel, en général par `copy(...)`.
     */
    protected fun ViewModel<State>.updateState(block: State.() -> State) {
        _state.update { block.invoke(it) }
    }

    /** This function is made as an extension because when we call it, it is yellow
     * and this is better for our UX
     *
     * @param obj événement à transmettre à l'écran, par exemple une `Destination`.
     */
    protected fun ViewModel<State>.sendEvent(obj: Any) {
        viewModelScope.launch {
            _events.send(obj)
        }
    }

    /** This function is made as an extension because when we call it, it is yellow
     * and this is better for our UX too
     *
     * Observe une source qui émet plusieurs fois (une table Realm, par exemple) : le travail se fait hors
     * du thread principal, et chaque résultat revient sur le thread principal pour pouvoir mettre l'état à jour.
     *
     * @param source flux à observer.
     * @param onResult appelé à chaque valeur reçue, ou une fois avec l'erreur si le flux échoue.
     */
    fun <T> AndroidViewModel.collectData(
        source: suspend () -> Flow<T>,
        onResult: Result<T>.() -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                source().collect { newValue ->
                    launch(Dispatchers.Main) { onResult(Result.success(newValue)) }
                }

            } catch (ex: Throwable) {
                launch(Dispatchers.Main) { onResult(Result.failure(ex)) }
            }

        }

    }

    /**
     * Charge une donnée une seule fois, hors du thread principal, puis rend le résultat sur le thread principal.
     *
     * @param source chargement à exécuter, par exemple un appel de repository.
     * @param onResult appelé une fois, avec la donnée ou avec l'erreur.
     */
    fun <T> AndroidViewModel.fetchData(
        source: suspend () -> T,
        onResult: Result<T>.() -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val success = source()
                launch(Dispatchers.Main) { onResult(Result.success(success)) }
            } catch (ex: Throwable) {
                launch(Dispatchers.Main) { onResult(Result.failure(ex)) }
            }

        }

    }

}