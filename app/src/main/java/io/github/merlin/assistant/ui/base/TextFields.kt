package io.github.merlin.assistant.ui.base

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                contentDescription = ""
            )
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
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = ""
            )
        }
        IconButton(
            onClick = { onValueChange(maxValue) },
        ) {
            Icon(imageVector = Icons.Rounded.KeyboardDoubleArrowRight, contentDescription = "")
        }

    }

}

@Composable
fun SliderNumberTextField(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    maxValue: Int = 100,
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Slider(
            modifier = Modifier
                .height(24.dp)
                .weight(1f),
            value = value.toFloat(),
            valueRange = 1f..maxValue.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = value.toString(),
            modifier = Modifier
                .width(50.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                val num = when {
                    it.isEmpty() -> 0
                    it.isDigitsOnly() -> it.toInt()
                    else -> 0
                }
                onValueChange(num)
            },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        ) { innerTextField ->
            Box(
                modifier = Modifier.border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
            ) {
                Box(modifier = Modifier.padding(8.dp)) {
                    innerTextField()
                }
            }
        }
    }
}