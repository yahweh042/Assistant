package io.github.merlin.assistant.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MenuListItem(
    title: String,
    options: Array<Option<T>>,
    value: T,
    onValueChange: (T) -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val option = options.findOption(value)

    ListItem(
        modifier = Modifier.clickable { expanded = true },
        headlineContent = { Text(text = title) },
        trailingContent = {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = option.label)
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option.label) },
                        onClick = {
                            expanded = false
                            if (option.value != value) {
                                onValueChange(value)
                            }
                        },
                    )
                }
            }
        }
    )

}