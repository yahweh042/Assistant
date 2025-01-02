package io.github.merlin.assistant.ui.screen.account

sealed class AccountAction {
    data object AddAccountButtonClick : AccountAction()
    data class SwitchAccountClick(val uid: String) : AccountAction()
}