package io.github.merlin.assistant.ui.screen.function.shop.page

import io.github.merlin.assistant.data.network.response.CommodityInfo
import io.github.merlin.assistant.ui.base.ViewState

data class ShopPageUiState(
    val viewState: ViewState = ViewState.Loading
) {

    data class ContentState(
        val commodityInfo: List<CommodityInfo>,
    )

}