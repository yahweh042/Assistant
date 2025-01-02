package io.github.merlin.assistant.ui.screen.login

sealed class LoginAction {
    data class LoginMenuClick(val cookie: String) : LoginAction()
}