package io.github.merlin.assistant.ui.screen.function.pot.arena

sealed class ArenaEvent {

    data class ShowToast(val msg: String): ArenaEvent()

}