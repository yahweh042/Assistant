package io.github.merlin.assistant.ui.screen.login

sealed class LoginEvent {
    data object NavigateBack : LoginEvent()
    data class ShowToast(val msg: String) : LoginEvent()
}