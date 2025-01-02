package io.github.merlin.assistant.ui.screen.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.local.model.Account
import io.github.merlin.assistant.repo.AccountRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    accountRepo: AccountRepo
) : AbstractViewModel<HomeUiState, HomeEvent, HomeAction>(
    initialState = run {
        HomeUiState()
    }
) {

    init {
        accountRepo.accountStateFlow
            .onEach { receiveAccountChange(it?.activeAccount) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.AccountCardClick -> handleAccountCardClick()
        }
    }

    private fun handleAccountCardClick() {
        sendEvent(HomeEvent.NavigateToAccount)
    }

    private fun receiveAccountChange(account: Account?) {
        mutableStateFlow.update {
            it.copy(account = account)
        }
        if (account == null) {
            sendEvent(HomeEvent.NavigateToAccount)
        }
    }


}