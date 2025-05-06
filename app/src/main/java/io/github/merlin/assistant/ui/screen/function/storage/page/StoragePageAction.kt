package io.github.merlin.assistant.ui.screen.function.storage.page

import io.github.merlin.assistant.data.network.response.GoodsInfo

sealed class StoragePageAction {

    data object RetryView: StoragePageAction()

    data class UseGoods(val goodsInfo: GoodsInfo, val num: Int) : StoragePageAction()
    data class AbandonGoods(val goodsInfo: GoodsInfo): StoragePageAction()

}