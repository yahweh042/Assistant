package io.github.merlin.assistant.ui.screen.function.pot.pack

sealed class PackEvent {

    data class ShowToast(val msg: String): PackEvent()

}