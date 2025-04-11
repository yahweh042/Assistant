package io.github.merlin.assistant.ui.screen.function.shop.page

sealed class ShopPageEvent {

    data class ShowToast(val msg: String): ShopPageEvent()

}