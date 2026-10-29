package org.mathieu.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

/**
 * Ligne qui présente un personnage par son avatar et son nom.
 * Partagée dans `core:ui` parce que la liste des personnages et la liste des résidents d'un lieu l'affichent,
 * et qu'une feature ne peut pas utiliser le code d'une autre. Elle reçoit des types simples, pas un `Character`,
 * pour rester indépendante du Domain.
 *
 * @param name nom affiché du personnage.
 * @param avatarUrl adresse de l'image du personnage.
 * @param modifier placement et comportement décidés par l'écran, par exemple le clic.
 */
@Composable
fun CharacterCard(
    name: String,
    avatarUrl: String,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .shadow(5.dp)
        // Couleur du thème et non blanc fixe : en mode sombre, le texte devient clair et resterait illisible sur du blanc.
        .background(MaterialTheme.colorScheme.surface)
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
) {

    SubcomposeAsyncImage(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape),
        model = avatarUrl,
        contentDescription = null
    )

    Spacer(modifier = Modifier.width(12.dp))

    Text(text = name)

}


@Preview
@Composable
private fun CharacterCardPreview() = PreviewContent {
    CharacterCard(name = "Rick Sanchez", avatarUrl = "")
}
