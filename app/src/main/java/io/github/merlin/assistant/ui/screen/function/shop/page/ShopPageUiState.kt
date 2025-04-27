package io.github.merlin.assistant.ui.screen.function.shop.page

import io.github.merlin.assistant.data.network.response.CommodityInfo
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.ViewState

data class ShopPageUiState(
    val viewState: ViewState<ShopPageState> = ViewState.Loading,
    val dialogState: CommodityInfoDialogState = CommodityInfoDialogState.Hide,
    val loadingDialogState: LoadingDialogState = LoadingDialogState.Nothing,
) {

    data class ShopPageState(
        val money: Int,
        val commodityInfo: List<CommodityInfo>,
    )

    sealed class CommodityInfoDialogState {
        data object Hide : CommodityInfoDialogState()
        data class Show(
            val commodityInfo: CommodityInfo,
            val num: Int = 1,
        ) : CommodityInfoDialogState()
    }

}