package org.mathieu.locations.details

import android.app.Application
import org.koin.core.component.inject
import org.mathieu.domain.models.character.Character
import org.mathieu.domain.repositories.LocationRepository
import org.mathieu.ui.ViewModel

/**
 * ViewModel de [LocationDetailsScreen] : charge le lieu complet, résidents compris.
 * L'écran n'a encore aucune action, donc pas de `handleAction` : il n'en aura un que le jour où l'utilisateur
 * pourra agir sur ce lieu.
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
 * Ce que l'écran de détail d'un lieu affiche : les mêmes données que sa carte, plus ses résidents.
 *
 * @property isLoading `true` tant que le lieu n'est pas chargé.
 * @property name nom du lieu.
 * @property type catégorie du lieu.
 * @property residents personnages vus en dernier dans ce lieu ; une liste vide veut dire qu'il n'y en a aucun.
 * @property error message d'erreur, `null` si tout va bien.
 */
data class LocationDetailsState(
    val isLoading: Boolean = true,
    val name: String = "",
    val type: String = "",
    val residents: List<Character> = emptyList(),
    val error: String? = null
)
