package io.github.merlin.assistant.ui.screen.function.jewel

sealed class JewelEvent {
    data class ShowToast(val msg: String) : JewelEvent()
}
