package io.github.merlin.assistant.ui.screen.function.pot.home

sealed class PotHomeEvent {
    data class ShowToast(val msg: String) : PotHomeEvent()
}
