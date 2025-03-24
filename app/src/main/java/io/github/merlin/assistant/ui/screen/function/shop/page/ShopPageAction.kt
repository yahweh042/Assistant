package io.github.merlin.assistant.ui.screen.function.shop.page

import io.github.merlin.assistant.data.network.response.CommodityInfo

sealed class ShopPageAction {

    data class RefreshShop(val shopType: String) : ShopPageAction()

    data object HideDialog: ShopPageAction()

    data class ShowDialog(val commodityInfo: CommodityInfo): ShopPageAction()

    data class UpdateGoodsNum(val commodityInfo: CommodityInfo, val num: Int) : ShopPageAction()

}