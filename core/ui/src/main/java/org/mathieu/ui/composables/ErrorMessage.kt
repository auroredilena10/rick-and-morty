package org.mathieu.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Message affiché à la place du contenu quand un écran n'a pas pu charger ses données.
 * Partagé pour que toutes les erreurs aient la même apparence.
 *
 * @param text message déjà traduit, lu par l'écran dans ses ressources.
 */
@Composable
fun ErrorMessage(text: String) = Text(
    modifier = Modifier.padding(16.dp),
    text = text,
    textAlign = TextAlign.Center,
    // Couleur principale du thème : violet foncé en mode clair, violet clair en mode sombre, lisible dans les deux.
    color = MaterialTheme.colorScheme.primary,
    fontSize = 32.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 36.sp
)


@Preview
@Composable
private fun ErrorMessagePreview() = PreviewContent {
    ErrorMessage(text = "This character could not be loaded.")
}
