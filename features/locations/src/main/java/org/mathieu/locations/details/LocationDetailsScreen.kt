package org.mathieu.locations.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.mathieu.domain.models.character.Character
import org.mathieu.domain.models.character.CharacterGender
import org.mathieu.domain.models.character.CharacterStatus
import org.mathieu.ui.R
import org.mathieu.ui.composables.BackTopBar
import org.mathieu.ui.composables.CharacterCard
import org.mathieu.ui.composables.ErrorMessage
import org.mathieu.ui.composables.LocationCard
import org.mathieu.ui.composables.PreviewContent

private typealias UIState = LocationDetailsState

/**
 * Écran de détail d'un lieu : il possède le ViewModel et lance le chargement du lieu demandé.
 *
 * @param navController contrôleur du `NavHost` de `MainActivity`, utilisé pour le retour.
 * @param id identifiant du lieu, lu dans les arguments de la destination.
 */
@Composable
fun LocationDetailsScreen(
    navController: NavController,
    id: Int
) {
    val viewModel: LocationDetailsViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    // Cycle de vie : LaunchedEffect(id) ne lance le chargement qu'à l'entrée de l'écran dans la composition,
    // au lieu de le relancer à chaque recomposition provoquée par le nouvel état.
    LaunchedEffect(id) { viewModel.init(locationId = id) }

    LocationDetailsContent(
        state = state,
        onClickBack = navController::popBackStack
    )

}


/**
 * Affichage du détail d'un lieu, sans ViewModel. Contrairement aux autres écrans, il montre un indicateur
 * de chargement : les résidents peuvent demander un appel réseau, l'écran resterait sinon vide un moment.
 *
 * @param state ce qu'il faut afficher.
 * @param onClickBack appelé au clic sur la flèche de retour.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationDetailsContent(
    state: UIState = UIState(),
    onClickBack: () -> Unit = { }
) = Scaffold(topBar = {
    BackTopBar(title = state.name, onClickBack = onClickBack)
}) { paddingValues ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()

            state.error != null -> ErrorMessage(text = stringResource(R.string.location_details_error))

            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {

                item {
                    // Pas de onClick : on est déjà sur ce lieu, la carte ne sert qu'à le présenter.
                    LocationCard(
                        modifier = Modifier.padding(8.dp),
                        name = state.name,
                        type = state.type
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        text = stringResource(R.string.location_details_residents),
                        fontWeight = FontWeight.Medium
                    )
                }

                if (state.residents.isEmpty()) item {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(R.string.location_details_no_residents)
                    )
                }

                items(state.residents) {
                    CharacterCard(
                        modifier = Modifier.padding(8.dp),
                        name = it.name,
                        avatarUrl = it.avatarUrl
                    )
                }

            }
        }
    }
}


@Preview
@Composable
private fun LocationDetailsPreview() = PreviewContent {
    LocationDetailsContent(
        state = UIState(
            isLoading = false,
            name = "Citadel of Ricks",
            type = "Space station",
            residents = listOf(
                Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = CharacterStatus.Alive,
                    species = "Human",
                    type = "",
                    gender = CharacterGender.Male,
                    origin = "Earth (C-137)" to 1,
                    location = null,
                    avatarUrl = ""
                )
            )
        )
    )
}
