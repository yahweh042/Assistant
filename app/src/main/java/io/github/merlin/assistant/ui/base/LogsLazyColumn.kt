package io.github.merlin.assistant.ui.base

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun LogsLazyColumn(
    modifier: Modifier = Modifier,
    logs: List<String>,
) {

    val lazyListState = rememberLazyListState()

    LaunchedEffect(key1 = logs) {
        if (logs.isNotEmpty()) {
            lazyListState.scrollToItem(logs.size - 1)
        }
    }

    LazyColumn(modifier = modifier, state = lazyListState) {
        items(logs) { log ->
            Text(text = log)
        }
    }
}