package io.github.merlin.assistant.ui.screen.function.shop.page

import io.github.merlin.assistant.data.network.response.CommodityInfo
import io.github.merlin.assistant.ui.base.ViewState

data class ShopPageUiState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: CommodityInfoDialogState = CommodityInfoDialogState.Hide,
) {

    data class ContentState(
        val commodityInfo: List<CommodityInfo>,
    )

    sealed class CommodityInfoDialogState {
        data object Hide : CommodityInfoDialogState()
        data class Show(
            val commodityInfo: CommodityInfo,
            val sliderPosition: Float = 0f,
        ) : CommodityInfoDialogState()
    }

}