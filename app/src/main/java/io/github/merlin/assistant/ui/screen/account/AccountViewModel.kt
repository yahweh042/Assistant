package io.github.merlin.assistant.ui.screen.account

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.local.model.AccountState
import io.github.merlin.assistant.repo.AccountRepo
import io.github.merlin.assistant.repo.LoginRepo
import io.github.merlin.assistant.repo.model.LoginResult
import io.github.merlin.assistant.repo.model.SwitchAccountResult
import io.github.merlin.assistant.ui.base.AbstractViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepo: AccountRepo,
    private val loginRepo: LoginRepo,
) : AbstractViewModel<AccountUiState, AccountEvent, AccountAction>(
    initialState = run {
        AccountUiState()
    }
) {

    init {
        accountRepo.accountStateFlow
            .onEach { receiveAccountListUpdate(it) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: AccountAction) {
        when (action) {
            AccountAction.AddAccountButtonClick -> handleAddAccountButtonClick()
            is AccountAction.SwitchAccountClick -> handleSwitchAccountClick(action)
            is AccountAction.EditAccount -> handleEditAccount(action)
            is AccountAction.ChangeToken -> handleChangeToken(action)
            AccountAction.UpdateToken -> handleUpdateToken(action)
            AccountAction.HideEditAccountDialog -> handleHideEditAccountDialog()
            is AccountAction.ConfirmCookie -> handleConfirmCookie(action)
            AccountAction.HideLoginWithCookieDialog -> handleHideLoginWithCookieDialog()
            is AccountAction.UpdateCookie -> handleUpdateCookie(action)
            AccountAction.ShowCookieDialog -> handleShowCookieDialog(action)
        }
    }

    private fun handleShowCookieDialog(action: AccountAction) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loginWithCookieState = AccountUiState.LoginWithCookieState.Show(""))
            }
        }
    }

    private fun handleUpdateCookie(action: AccountAction.UpdateCookie) {
        viewModelScope.launch {
            if (state.loginWithCookieState is AccountUiState.LoginWithCookieState.Show) {
                mutableStateFlow.update {
                    it.copy(loginWithCookieState = AccountUiState.LoginWithCookieState.Show(action.cookie))
                }
            }
        }
    }

    private fun handleHideLoginWithCookieDialog() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loginWithCookieState = AccountUiState.LoginWithCookieState.Hide)
            }
        }
    }

    private fun handleConfirmCookie(action: AccountAction.ConfirmCookie) {
        viewModelScope.launch {
            when (val loginResult = loginRepo.loginWithCookie(action.cookie)) {
                LoginResult.Success -> mutableStateFlow.update {
                    it.copy(loginWithCookieState = AccountUiState.LoginWithCookieState.Hide)
                }

                is LoginResult.Error -> sendEvent(AccountEvent.ShowToast(loginResult.msg))
            }
        }
    }

    private fun handleHideEditAccountDialog() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(editAccountDialogState = AccountUiState.EditAccountDialogState.Hide)
            }
        }
    }

    private fun handleUpdateToken(action: AccountAction) {
        viewModelScope.launch {
            when (val dialogState = state.editAccountDialogState) {
                AccountUiState.EditAccountDialogState.Hide -> Unit
                is AccountUiState.EditAccountDialogState.Show -> {
                    accountRepo.insertAccount(dialogState.account)
                    mutableStateFlow.update {
                        it.copy(editAccountDialogState = AccountUiState.EditAccountDialogState.Hide)
                    }
                }
            }
        }
    }

    private fun handleChangeToken(action: AccountAction.ChangeToken) {
        viewModelScope.launch {
            when (val dialogState = state.editAccountDialogState) {
                AccountUiState.EditAccountDialogState.Hide -> Unit
                is AccountUiState.EditAccountDialogState.Show -> mutableStateFlow.update {
                    it.copy(
                        editAccountDialogState = AccountUiState.EditAccountDialogState.Show(
                            dialogState.account.copy(token = action.token)
                        )
                    )
                }
            }
        }
    }

    private fun handleEditAccount(account: AccountAction.EditAccount) {
        mutableStateFlow.update {
            it.copy(editAccountDialogState = AccountUiState.EditAccountDialogState.Show(account.account))
        }
    }

    private fun handleSwitchAccountClick(action: AccountAction.SwitchAccountClick) {
        viewModelScope.launch {
            when (val switchResult = accountRepo.switchAccount(action.uid)) {
                SwitchAccountResult.Switched -> sendEvent(AccountEvent.ShowToast("切换成功"))
                SwitchAccountResult.NoChange -> Unit
                is SwitchAccountResult.SwitchError -> sendEvent(AccountEvent.ShowToast(switchResult.msg))
            }
        }
    }

    private fun handleAddAccountButtonClick() {
        sendEvent(AccountEvent.NavigateToLogin)
    }

    private fun receiveAccountListUpdate(accountState: AccountState?) {
        mutableStateFlow.update { state ->
            state.copy(accounts = accountState?.accounts?.values?.map { it.copy(isActive = accountState.activeUid == it.uid) }
                ?: listOf())
        }
    }

}