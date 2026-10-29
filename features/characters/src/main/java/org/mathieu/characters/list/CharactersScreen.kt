package org.mathieu.characters.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.mathieu.ui.Destination
import org.mathieu.ui.R
import org.mathieu.ui.composables.CharacterCard
import org.mathieu.ui.composables.ErrorMessage
import org.mathieu.ui.navigate
import org.mathieu.ui.theme.Purple40

private typealias UIState = CharactersState
private typealias UIAction = CharactersAction

@Composable
fun CharactersScreen(navController: NavController) {
    val viewModel: CharactersViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events
            .onEach { event ->
                if (event is Destination.CharacterDetails)
                    navController.navigate(destination = event)
            }.collect()
    }

    CharactersContent(
        state = state,
        onAction = viewModel::handleAction
    )

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun CharactersContent(
    state: UIState = UIState(),
    onAction: (UIAction) -> Unit = { }
) = Scaffold(topBar = {
    Text(
        modifier = Modifier
            .background(Purple40)
            .padding(16.dp)
            .fillMaxWidth(),
        text = stringResource(R.string.characters_title),
        textAlign = TextAlign.Center,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    )
}) { paddingValues ->

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = state.error != null, label = "") {
            state.error?.let {
                ErrorMessage(text = stringResource(R.string.characters_error))
            } ?: LazyColumn {

                items(state.characters) {
                    CharacterCard(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                onAction(CharactersAction.SelectedCharacter(it))
                            },
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
private fun CharactersPreview() = org.mathieu.ui.composables.PreviewContent {
    CharactersContent()
}

