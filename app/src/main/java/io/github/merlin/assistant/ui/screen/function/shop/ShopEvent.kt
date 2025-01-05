package io.github.merlin.assistant.ui.screen.function.shop

sealed class ShopEvent {

    data class ScrollToPage(val page: Int): ShopEvent()

}