package org.mathieu.locations.details

import android.app.Application
import org.koin.core.component.inject
import org.mathieu.domain.models.character.Character
import org.mathieu.domain.repositories.LocationRepository
import org.mathieu.ui.ViewModel

/**
 * Le ViewModel de [LocationDetailsScreen] : il charge le lieu complet, avec ses résidents.
 * Pas de `handleAction` ici, parce que l'écran n'a aucune action pour l'instant. On l'ajoutera
 * le jour où l'utilisateur pourra faire quelque chose sur ce lieu (cliquer sur un résident, par exemple).
 *
 * @param application nécessaire à `AndroidViewModel`, fournie par `viewModel()`.
 */
class LocationDetailsViewModel(application: Application) : ViewModel<LocationDetailsState>(
    LocationDetailsState(), application) {

    private val locationRepository: LocationRepository by inject()

    /**
     * Charge le lieu affiché.
     *
     * @param locationId identifiant reçu dans les arguments de la destination.
     */
    fun init(locationId: Int) {
        fetchData(
            source = { locationRepository.getLocation(id = locationId) }
        ) {

            onSuccess {
                updateState { copy(name = it.name, type = it.type, residents = it.residents, error = null) }
            }

            onFailure {
                updateState { copy(error = it.toString()) }
            }

            updateState { copy(isLoading = false) }
        }
    }

}


/**
 * Ce que l'écran de détail d'un lieu affiche : les mêmes infos que sa carte, plus ses résidents.
 *
 * @property isLoading `true` tant que le lieu n'est pas chargé.
 * @property name nom du lieu.
 * @property type catégorie du lieu.
 * @property residents les personnages vus en dernier dans ce lieu ; une liste vide veut dire qu'il n'y en a aucun.
 * @property error message d'erreur, `null` si tout va bien.
 */
data class LocationDetailsState(
    val isLoading: Boolean = true,
    val name: String = "",
    val type: String = "",
    val residents: List<Character> = emptyList(),
    val error: String? = null
)
