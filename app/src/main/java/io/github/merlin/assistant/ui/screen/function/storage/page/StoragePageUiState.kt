package io.github.merlin.assistant.ui.screen.function.storage.page

import io.github.merlin.assistant.data.network.response.GoodsInfo
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.ViewState

data class StoragePageUiState(
    val viewState: ViewState<StoragePageContentState> = ViewState.Loading,
    val loadingDialogState: LoadingDialogState = LoadingDialogState.Nothing,
) {

    data class StoragePageContentState(
        val goodsInfos: List<GoodsInfo> = listOf()
    )

}