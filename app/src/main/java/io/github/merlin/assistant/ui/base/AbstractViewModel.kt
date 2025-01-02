package io.github.merlin.assistant.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class AbstractViewModel<State, Event, Action>(
    initialState: State,
) : ViewModel() {
    protected val mutableStateFlow: MutableStateFlow<State> = MutableStateFlow(initialState)
    private val eventChannel: Channel<Event> = Channel(capacity = Channel.UNLIMITED)
    private val internalActionChannel: Channel<Action> = Channel(capacity = Channel.UNLIMITED)

    protected val state: State get() = mutableStateFlow.value

    val stateFlow: StateFlow<State> = mutableStateFlow.asStateFlow()

    val eventFlow: Flow<Event> = eventChannel.receiveAsFlow()

    private val actionChannel: SendChannel<Action> = internalActionChannel

    init {
        viewModelScope.launch {
            internalActionChannel
                .consumeAsFlow()
                .collect { action ->
                    handleAction(action)
                }
        }
    }

    protected abstract fun handleAction(action: Action): Unit

    fun trySendAction(action: Action) {
        actionChannel.trySend(action)
    }

    protected suspend fun sendAction(action: Action) {
        actionChannel.send(action)
    }

    protected fun sendEvent(event: Event) {
        viewModelScope.launch { eventChannel.send(event) }
    }
}
