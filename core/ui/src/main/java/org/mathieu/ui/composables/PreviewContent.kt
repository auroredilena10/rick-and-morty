package org.mathieu.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.mathieu.ui.theme.LeTheme

@Composable
fun PreviewContent(content: @Composable BoxScope.() -> Unit) {
    LeTheme {
        Box(modifier = Modifier.background(Color.White), content = content)
    }
}
