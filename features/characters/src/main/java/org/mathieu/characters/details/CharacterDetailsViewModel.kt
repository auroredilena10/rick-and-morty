package org.mathieu.characters.details

import android.app.Application
import org.koin.core.component.inject
import org.mathieu.domain.managers.LinkManager
import org.mathieu.domain.managers.VibrationManager
import org.mathieu.domain.models.location.LocationPreview
import org.mathieu.domain.repositories.CharacterRepository
import org.mathieu.ui.Destination
import org.mathieu.ui.ViewModel

/** 50 ms : assez pour sentir la vibration, assez court pour ne pas gêner. C'est une confirmation, pas une alerte. */
private const val LOCATION_CLICK_VIBRATION_MS = 50L

/** Ce que l'utilisateur peut faire sur l'écran de détail d'un personnage. */
sealed interface CharacterDetailsAction {
    /** L'utilisateur a touché la carte du dernier lieu connu. */
    object SelectedLocation : CharacterDetailsAction

    /** L'utilisateur a touché l'avatar, pour le voir en grand. */
    object SelectedAvatar : CharacterDetailsAction
}

/**
 * Le ViewModel de [CharacterDetailsScreen] : il charge le personnage et décide quoi faire pour chaque action.
 * C'est lui qui fait vibrer, pas l'écran : l'UI connaît l'action, jamais le matériel.
 *
 * @param application nécessaire à `AndroidViewModel`, fournie par `viewModel()`.
 */
class CharacterDetailsViewModel(application: Application) : ViewModel<CharacterDetailsState>(
    CharacterDetailsState(), application) {

    private val characterRepository: CharacterRepository by inject()
    private val vibrationManager: VibrationManager by inject()
    private val linkManager: LinkManager by inject()

    /**
     * Charge le personnage affiché.
     *
     * @param characterId identifiant reçu dans les arguments de la destination.
     */
    fun init(characterId: Int) {
        fetchData(
            source = { characterRepository.getCharacter(id = characterId) }
        ) {

            onSuccess {
                updateState { copy(avatarUrl = it.avatarUrl, name = it.name, location = it.location, error = null) }
            }

            onFailure {
                updateState { copy(error = it.toString()) }
            }

            updateState { copy(isLoading = false) }
        }
    }

    fun handleAction(action: CharacterDetailsAction) {
        when (action) {
            CharacterDetailsAction.SelectedLocation -> selectedLocation()
            CharacterDetailsAction.SelectedAvatar -> selectedAvatar()
        }
    }


    /** Vibre d'abord, pour que le clic soit senti tout de suite, puis demande à l'écran d'ouvrir le lieu. */
    private fun selectedLocation() {
        val location = state.value.location ?: return
        vibrationManager.vibrate(LOCATION_CLICK_VIBRATION_MS)
        sendEvent(Destination.LocationDetails(location.id.toString()))
    }

    /** Ouvre l'image dans une autre app (le navigateur) : pas besoin de coder un écran de zoom pour ça. */
    private fun selectedAvatar() {
        val avatarUrl = state.value.avatarUrl
        if (avatarUrl.isNotBlank()) linkManager.open(avatarUrl)
    }

}


/**
 * Ce que l'écran de détail d'un personnage affiche.
 *
 * @property isLoading `true` tant que le personnage n'est pas chargé.
 * @property avatarUrl adresse de l'image du personnage.
 * @property name nom du personnage.
 * @property location le dernier lieu connu, affiché dans une carte ; `null` s'il est inconnu, et la carte n'apparaît pas.
 * @property error message d'erreur, `null` si tout va bien.
 */
data class CharacterDetailsState(
    val isLoading: Boolean = true,
    val avatarUrl: String = "",
    val name: String = "",
    val location: LocationPreview? = null,
    val error: String? = null
)
