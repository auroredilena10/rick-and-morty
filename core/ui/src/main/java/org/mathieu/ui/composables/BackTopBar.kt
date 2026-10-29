package org.mathieu.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mathieu.ui.R
import org.mathieu.ui.theme.Purple40

/**
 * Barre du haut des écrans de détail : une flèche de retour et un titre.
 * Partagée pour que tous les écrans de détail aient la même barre, sans en recopier le code.
 *
 * @param title texte affiché à côté de la flèche, en général le nom de ce que montre l'écran.
 * @param onClickBack appelé au clic sur la flèche ; c'est l'écran qui décide où revenir.
 */
@Composable
fun BackTopBar(
    title: String,
    onClickBack: () -> Unit
) = Row(
    modifier = Modifier
        .background(Purple40)
        .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
) {
    Image(
        modifier = Modifier
            .padding(16.dp)
            .clickable(onClick = onClickBack),
        imageVector = Icons.Default.ArrowBack,
        contentDescription = stringResource(R.string.top_bar_back),
        colorFilter = ColorFilter.tint(Color.White)
    )

    Text(
        text = title,
        textAlign = TextAlign.Center,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    )
}


@Preview
@Composable
private fun BackTopBarPreview() = PreviewContent {
    BackTopBar(title = "Rick Sanchez", onClickBack = { })
}
