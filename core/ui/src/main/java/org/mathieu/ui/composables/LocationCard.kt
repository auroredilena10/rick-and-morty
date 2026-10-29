package org.mathieu.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Carte qui présente un lieu par son nom et son type.
 * Partagée dans `core:ui` parce que le détail d'un personnage et le détail d'un lieu l'affichent.
 * Elle signale seulement le clic : c'est l'écran qui décide de ce qu'il veut dire (naviguer, vibrer, ou rien).
 *
 * @param name nom du lieu.
 * @param type catégorie du lieu (planète, station spatiale…).
 * @param modifier placement décidé par l'écran.
 * @param onClick appelé au clic ; `null` rend la carte non cliquable, par exemple quand on est déjà sur ce lieu.
 */
@Composable
fun LocationCard(
    name: String,
    type: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) = Column(
    modifier = modifier
        .shadow(5.dp)
        // Couleurs du thème et non fixes, pour rester lisible en mode clair comme en mode sombre.
        .background(MaterialTheme.colorScheme.surface)
        .fillMaxWidth()
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        .padding(horizontal = 16.dp, vertical = 12.dp)
) {

    Text(text = name, fontWeight = FontWeight.Medium)

    Text(text = type, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

}


@Preview
@Composable
private fun LocationCardPreview() = PreviewContent {
    LocationCard(name = "Citadel of Ricks", type = "Space station", onClick = { })
}
