package io.github.merlin.assistant.ui.screen.function.pot.exchange

import io.github.merlin.assistant.data.network.response.QueryExchangeResponse
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.ViewState

data class ExchangeUiState(
    val viewState: ViewState<List<QueryExchangeResponse.Exchange>> = ViewState.Loading,
    val loadingDialogState: LoadingDialogState = LoadingDialogState.Nothing,
)
