package io.github.merlin.assistant.ui.screen.login

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.repo.LoginRepo
import io.github.merlin.assistant.repo.model.LoginResult
import io.github.merlin.assistant.ui.base.AbstractViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepo: LoginRepo,
) : AbstractViewModel<LoginUiState, LoginEvent, LoginAction>(
    initialState = run {
        LoginUiState()
    }
) {

    override fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.LoginMenuClick -> handleLoginMenuClick(action)
        }
    }

    private fun handleLoginMenuClick(action: LoginAction.LoginMenuClick) {
        val cookie: String = action.cookie
        viewModelScope.launch {
            when (val loginResult = loginRepo.loginWithCookie(cookie)) {
                LoginResult.Success -> sendEvent(LoginEvent.NavigateBack)
                is LoginResult.Error -> sendEvent(LoginEvent.ShowToast(loginResult.msg))
            }
        }
    }

}