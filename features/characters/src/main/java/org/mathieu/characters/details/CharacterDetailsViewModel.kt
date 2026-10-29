package org.mathieu.characters.details

import android.app.Application
import org.koin.core.component.inject
import org.mathieu.domain.managers.LinkManager
import org.mathieu.domain.managers.VibrationManager
import org.mathieu.domain.models.location.LocationPreview
import org.mathieu.domain.repositories.CharacterRepository
import org.mathieu.ui.Destination
import org.mathieu.ui.ViewModel

/** Assez long pour être senti, assez court pour ne pas gêner : c'est une confirmation, pas une alerte. */
private const val LOCATION_CLICK_VIBRATION_MS = 50L

/** Ce que l'utilisateur peut faire sur l'écran de détail d'un personnage. */
sealed interface CharacterDetailsAction {
    /** L'utilisateur a touché la carte du dernier lieu connu. */
    object SelectedLocation : CharacterDetailsAction

    /** L'utilisateur a touché l'avatar, pour le voir en grand. */
    object SelectedAvatar : CharacterDetailsAction
}

/**
 * ViewModel de [CharacterDetailsScreen] : charge le personnage et décide de ce que veut dire chaque action.
 * C'est lui, et non l'écran, qui fait vibrer : l'UI ne connaît que l'action, jamais le matériel.
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


    /** Vibre d'abord pour confirmer le toucher tout de suite, puis demande à l'écran d'ouvrir le lieu. */
    private fun selectedLocation() {
        val location = state.value.location ?: return
        vibrationManager.vibrate(LOCATION_CLICK_VIBRATION_MS)
        sendEvent(Destination.LocationDetails(location.id.toString()))
    }

    /** Confie l'image à une autre application plutôt que de créer un écran de zoom qui n'apporterait rien de plus. */
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
 * @property location dernier lieu connu, affiché en carte ; `null` quand il est inconnu, la carte est alors cachée.
 * @property error message d'erreur, `null` si tout va bien.
 */
data class CharacterDetailsState(
    val isLoading: Boolean = true,
    val avatarUrl: String = "",
    val name: String = "",
    val location: LocationPreview? = null,
    val error: String? = null
)
