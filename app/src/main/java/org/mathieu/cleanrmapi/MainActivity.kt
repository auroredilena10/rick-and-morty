package org.mathieu.cleanrmapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.mathieu.characters.details.CharacterDetailsScreen
import org.mathieu.characters.list.CharactersScreen
import org.mathieu.locations.details.LocationDetailsScreen
import org.mathieu.ui.Destination
import org.mathieu.ui.composable
import org.mathieu.ui.theme.LeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    content = { MainContent() }
                )
            }
        }
    }
}


@Composable
private fun MainContent() {

    val navController = rememberNavController()

    //https://developer.android.com/jetpack/compose/navigation?hl=fr
    NavHost(navController = navController, startDestination = "characters") {

        composable(Destination.Characters) {
            CharactersScreen(
                navController
            )
        }

        composable(
            destination = Destination.CharacterDetails()
        ) { backStackEntry ->

            CharacterDetailsScreen(
                navController = navController,
                id = backStackEntry.arguments?.getInt("characterId") ?: -1
            )

        }

        composable(
            destination = Destination.LocationDetails()
        ) { backStackEntry ->

            LocationDetailsScreen(
                navController = navController,
                id = backStackEntry.arguments?.getInt("locationId") ?: -1
            )

        }

    }

}
