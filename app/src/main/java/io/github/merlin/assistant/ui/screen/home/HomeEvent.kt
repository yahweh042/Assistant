package io.github.merlin.assistant.ui.screen.home

sealed class HomeEvent {

    data object NavigateToAccount : HomeEvent()

}