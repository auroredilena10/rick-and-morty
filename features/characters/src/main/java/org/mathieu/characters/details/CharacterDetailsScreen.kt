package org.mathieu.characters.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.mathieu.domain.models.location.LocationPreview
import org.mathieu.ui.Destination
import org.mathieu.ui.R
import org.mathieu.ui.composables.BackTopBar
import org.mathieu.ui.composables.ErrorMessage
import org.mathieu.ui.composables.LocationCard
import org.mathieu.ui.composables.PreviewContent
import org.mathieu.ui.navigate

private typealias UIState = CharacterDetailsState
private typealias UIAction = CharacterDetailsAction

/**
 * L'écran de détail d'un personnage. Il a le ViewModel, lui envoie les actions et navigue quand il reçoit un événement.
 *
 * @param navController le contrôleur du `NavHost` de `MainActivity`.
 * @param id l'id du personnage, lu dans les arguments de la destination.
 */
@Composable
fun CharacterDetailsScreen(
    navController: NavController,
    id: Int
) {
    val viewModel: CharacterDetailsViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    // Cycle de vie : LaunchedEffect(id) ne lance le chargement qu'à l'entrée de l'écran dans la composition,
    // ou si l'identifiant change, au lieu de le relancer à chaque recomposition provoquée par le nouvel état.
    LaunchedEffect(id) { viewModel.init(characterId = id) }

    // Cycle de vie : cette écoute s'arrête toute seule quand on quitte l'écran. Un événement qui arriverait
    // après ne peut donc plus faire naviguer depuis un écran fermé.
    LaunchedEffect(viewModel) {
        viewModel.events
            .onEach { event ->
                if (event is Destination.LocationDetails)
                    navController.navigate(destination = event)
            }.collect()
    }

    CharacterDetailsContent(
        state = state,
        onClickBack = navController::popBackStack,
        onAction = viewModel::handleAction
    )

}


/**
 * L'affichage du détail d'un personnage, sans ViewModel : il reçoit juste l'état et des callbacks.
 * Comme ça on peut le voir en Preview avec n'importe quel état.
 *
 * @param state ce qu'il faut afficher.
 * @param onClickBack appelé au clic sur la flèche de retour.
 * @param onAction reçoit les actions de l'utilisateur, à envoyer au ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun CharacterDetailsContent(
    state: UIState = UIState(),
    onClickBack: () -> Unit = { },
    onAction: (UIAction) -> Unit = { }
) = Scaffold(topBar = {
    BackTopBar(title = state.name, onClickBack = onClickBack)
}) { paddingValues ->
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues), contentAlignment = Alignment.Center) {
        AnimatedContent(targetState = state.error != null, label = "") {
            state.error?.let {
                ErrorMessage(text = stringResource(R.string.character_details_error))
            } ?: Box(modifier = Modifier.fillMaxSize()) {

                Box(Modifier.align(Alignment.TopCenter)) {

                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .blur(100.dp)
                            .alpha(0.3f)
                            .fillMaxWidth(),
                        model = state.avatarUrl,
                        contentDescription = null
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            )
                    )


                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .shadow(3.dp)
                            .clickable { onAction(CharacterDetailsAction.SelectedAvatar) },
                        model = state.avatarUrl,
                        // Une image cliquable a besoin d'une description : sinon un lecteur d'écran ne dit pas à quoi sert le clic.
                        contentDescription = stringResource(R.string.character_details_open_avatar)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = state.name)

                    state.location?.let { location ->
                        LocationCard(
                            modifier = Modifier.padding(16.dp),
                            name = location.name,
                            type = location.type,
                            onClick = { onAction(CharacterDetailsAction.SelectedLocation) }
                        )
                    }
                }


            }
        }
    }
}


@Preview
@Composable
private fun CharacterDetailsPreview() = PreviewContent {
    CharacterDetailsContent(
        state = UIState(
            isLoading = false,
            name = "Rick Sanchez",
            location = LocationPreview(id = 3, name = "Citadel of Ricks", type = "Space station", dimension = "unknown")
        )
    )
}

