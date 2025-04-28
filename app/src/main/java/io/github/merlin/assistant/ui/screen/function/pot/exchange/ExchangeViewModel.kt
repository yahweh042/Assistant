package io.github.merlin.assistant.ui.screen.function.pot.exchange

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.repo.PotRepo
import io.github.merlin.assistant.repo.model.QueryExchangeResult
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val potRepo: PotRepo
) : AbstractViewModel<ExchangeUiState, ExchangeEvent, ExchangeAction>(
    initialState = run {
        ExchangeUiState()
    }
) {

    init {
        viewModelScope.launch {
            queryExchange()
        }
    }

    override fun handleAction(action: ExchangeAction) {
        when (action) {
            ExchangeAction.RetryQueryExchange -> handleRetryQueryExchange()
            is ExchangeAction.Exchange -> handleExchange(action)
        }
    }

    private fun handleExchange(action: ExchangeAction.Exchange) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("兑换中"))
            }
            val exchangeResponse = potRepo.mysteryExchange(action.id)
            if (exchangeResponse.result == 0) {
                queryExchange()
                sendEvent(ExchangeEvent.ShowToast("兑换成功"))
            } else {
                sendEvent(ExchangeEvent.ShowToast("兑换失败 ${exchangeResponse.msg}"))
            }
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Nothing)
            }
        }
    }

    private fun handleRetryQueryExchange() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Loading)
            }
            queryExchange()
        }
    }

    private suspend fun queryExchange() {
        when (val queryExchangeResult = potRepo.queryExchange()) {
            is QueryExchangeResult.Success -> mutableStateFlow.update {
                it.copy(viewState = ViewState.Success(queryExchangeResult.exchange))
            }

            is QueryExchangeResult.Error -> mutableStateFlow.update {
                it.copy(viewState = ViewState.Error(queryExchangeResult.msg))
            }
        }
    }


}