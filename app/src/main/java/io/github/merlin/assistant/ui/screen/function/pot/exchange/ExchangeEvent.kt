package io.github.merlin.assistant.ui.screen.function.pot.exchange

sealed class ExchangeEvent {

    data class ShowToast(val msg: String): ExchangeEvent()

}