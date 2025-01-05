package io.github.merlin.assistant.ui.screen.function.shop.page

sealed class ShopPageAction {

    data class RefreshShop(val shopType: String) : ShopPageAction()

}