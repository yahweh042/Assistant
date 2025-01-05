package io.github.merlin.assistant.ui.screen.function.shop

sealed class ShopAction {

    data class SelectedTabIndexChange(val tabIndex: Int) : ShopAction()

}