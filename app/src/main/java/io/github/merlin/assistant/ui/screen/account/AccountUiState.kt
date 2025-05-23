package io.github.merlin.assistant.ui.screen.account

import io.github.merlin.assistant.data.local.model.Account

data class AccountUiState(
    val accounts: List<Account> = listOf(),
    val editAccountDialogState: EditAccountDialogState = EditAccountDialogState.Hide,
    val loginWithCookieState: LoginWithCookieState = LoginWithCookieState.Hide,
) {

    sealed class EditAccountDialogState {
        data class Show(val account: Account) : EditAccountDialogState()
        data object Hide : EditAccountDialogState()
    }

    sealed class LoginWithCookieState {
        data class Show(val cookie: String) : LoginWithCookieState()
        data object Hide : LoginWithCookieState()
    }

}