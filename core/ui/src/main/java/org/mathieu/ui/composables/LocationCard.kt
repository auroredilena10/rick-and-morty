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
 * La carte d'un lieu, avec son nom et son type.
 * Elle est dans `core:ui` parce que le détail d'un personnage et le détail d'un lieu l'affichent tous les deux.
 * Elle prévient juste qu'on a cliqué : c'est l'écran qui décide quoi faire (naviguer, vibrer, ou rien).
 *
 * @param name le nom du lieu.
 * @param type le type du lieu (planète, station spatiale…).
 * @param modifier ce que l'écran veut ajouter, par exemple une marge.
 * @param onClick appelé au clic ; avec `null`, la carte n'est pas cliquable (quand on est déjà sur ce lieu).
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
        // Couleurs du thème et pas fixes, pour être lisible en mode clair comme en mode sombre.
        .background(MaterialTheme.colorScheme.surface)
        .fillMaxWidth()
        // On n'ajoute le clic que s'il y a un onClick ; sinon `Modifier` tout seul n'ajoute rien.
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
