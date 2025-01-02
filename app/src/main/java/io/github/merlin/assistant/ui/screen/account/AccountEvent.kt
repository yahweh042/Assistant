package io.github.merlin.assistant.ui.screen.account

sealed class AccountEvent {
    data object NavigateToLogin : AccountEvent()
    data class ShowToast(val msg: String) : AccountEvent()
}