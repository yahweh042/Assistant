package io.github.merlin.assistant.ui.screen.function.pot.pack

import io.github.merlin.assistant.data.network.response.GoodsInfo

sealed class PackAction {
    data object RetryViewPack : PackAction()
    data object PullToRefresh : PackAction()
    data class UseGoods(val goodsInfo: GoodsInfo) : PackAction()

}