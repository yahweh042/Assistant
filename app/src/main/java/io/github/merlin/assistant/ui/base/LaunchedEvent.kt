package io.github.merlin.assistant.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun <Event> LaunchedEvent(
    viewModel: AbstractViewModel<*, Event, *>,
    handler: suspend (Event) -> Unit,
) {

    LaunchedEffect(key1 = viewModel) {
        viewModel.eventFlow
            .onEach { handler.invoke(it) }
            .launchIn(this)
    }

}