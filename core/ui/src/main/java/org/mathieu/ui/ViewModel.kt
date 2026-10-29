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
 * La base de tous les ViewModels d'écran. Elle met en place le MVI : les données vont dans un seul sens.
 * L'écran lit [state] (ce qu'il affiche) et écoute [events] (ce qu'il doit faire une seule fois, comme naviguer).
 * Le ViewModel ne les change qu'avec [updateState] et [sendEvent].
 * Elle hérite de `KoinComponent` pour que chaque ViewModel récupère ses repositories et Managers avec `by inject()`.
 *
 * @param State le type de l'état de l'écran, une `data class`.
 * @param initialState état affiché avant tout chargement.
 * @param application nécessaire à `AndroidViewModel`, fournie par `viewModel()`.
 * @property state l'état actuel, que l'écran observe.
 * @property events les événements à faire une seule fois. C'est un `Channel` et pas un état : sinon une navigation
 * serait refaite à chaque recomposition de l'écran.
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
     * @param block donne le nouvel état à partir de l'ancien, en général avec `copy(...)`.
     */
    protected fun ViewModel<State>.updateState(block: State.() -> State) {
        _state.update { block.invoke(it) }
    }

    /** This function is made as an extension because when we call it, it is yellow
     * and this is better for our UX
     *
     * @param obj l'événement à envoyer à l'écran, par exemple une `Destination`.
     */
    protected fun ViewModel<State>.sendEvent(obj: Any) {
        viewModelScope.launch {
            _events.send(obj)
        }
    }

    /** This function is made as an extension because when we call it, it is yellow
     * and this is better for our UX too
     *
     * Observe une source qui renvoie plusieurs valeurs dans le temps (une table Realm, par exemple).
     * Le travail se fait en arrière-plan (`Dispatchers.IO`), et chaque résultat revient sur le thread
     * principal (`Dispatchers.Main`), le seul qui a le droit de mettre l'écran à jour.
     *
     * @param source le flux à observer.
     * @param onResult appelé à chaque nouvelle valeur, ou une fois avec l'erreur si le flux plante.
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
     * Charge une donnée une seule fois, en arrière-plan, puis renvoie le résultat sur le thread principal.
     *
     * @param source le chargement à faire, par exemple un appel au repository.
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