package io.github.merlin.assistant.ui.screen.function.pot

sealed class PotEvent {
    data class ShowToast(val msg: String) : PotEvent()
    data object ShowBottomSheet : PotEvent()
    data object HideBottomSheet : PotEvent()
}
