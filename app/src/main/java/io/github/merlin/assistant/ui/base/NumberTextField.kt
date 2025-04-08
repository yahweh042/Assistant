package io.github.merlin.assistant.ui.base

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly

@Composable
fun NumberTextField(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    maxValue: Int = 100,
) {

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onValueChange(0) }) {
            Icon(imageVector = Icons.Rounded.KeyboardDoubleArrowLeft, contentDescription = "")
        }
        IconButton(
            onClick = { onValueChange(value - 1) },
        ) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "")
        }
        BasicTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(IntrinsicSize.Min),
            value = value.toString(),
            singleLine = true,
            onValueChange = {
                val num = when {
                    it.isEmpty() -> 0
                    it.isDigitsOnly() -> it.toInt()
                    else -> 0
                }
                onValueChange(num)
            }
        )
        IconButton(
            onClick = { onValueChange(value + 1) },
        ) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "")
        }
        IconButton(
            onClick = { onValueChange(maxValue) },
        ) {
            Icon(imageVector = Icons.Rounded.KeyboardDoubleArrowRight, contentDescription = "")
        }

    }

}