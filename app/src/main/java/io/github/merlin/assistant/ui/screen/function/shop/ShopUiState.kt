package io.github.merlin.assistant.ui.screen.function.shop

import androidx.compose.foundation.pager.PagerState
import io.github.merlin.assistant.data.local.model.ShopType
import io.github.merlin.assistant.data.network.response.CommodityInfo
import io.github.merlin.assistant.ui.base.ViewState

data class ShopUiState(
    val shopTypes: List<ShopType> = listOf(),
    val pagerState: PagerState? = null,
    val viewState: ViewState = ViewState.Loading,
) {

    data class ShopContentState(
        val commodityInfo: List<CommodityInfo> = listOf()
    )

}