package io.github.merlin.assistant.ui.screen.account

import io.github.merlin.assistant.data.local.model.Account

sealed class AccountAction {
    data object AddAccountButtonClick : AccountAction()
    data class SwitchAccountClick(val uid: String) : AccountAction()
    data class EditAccount(val account: Account) : AccountAction()

    data class ChangeToken(val token: String) : AccountAction()
    data object UpdateToken : AccountAction()
    data object HideEditAccountDialog: AccountAction()
}