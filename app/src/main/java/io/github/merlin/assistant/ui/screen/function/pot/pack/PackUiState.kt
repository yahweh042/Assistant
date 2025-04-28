package io.github.merlin.assistant.ui.screen.function.pot.pack

import io.github.merlin.assistant.data.network.response.GoodsInfo
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.ViewState

data class PackUiState(
    val isRefreshing: Boolean = false,
    val viewState: ViewState<List<GoodsInfo>> = ViewState.Loading,
    val loadingDialogState: LoadingDialogState = LoadingDialogState.Nothing,
)
