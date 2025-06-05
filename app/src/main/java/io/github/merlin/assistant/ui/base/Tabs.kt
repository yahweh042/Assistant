package io.github.merlin.assistant.ui.base

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun TabTextButton(
    text: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        colors = if (active) ButtonDefaults.elevatedButtonColors() else ButtonDefaults.textButtonColors(),
    ) {
        Text(text = text)
    }
}