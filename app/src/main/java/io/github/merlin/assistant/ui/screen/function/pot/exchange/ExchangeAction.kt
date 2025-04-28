package io.github.merlin.assistant.ui.screen.function.pot.exchange

sealed class ExchangeAction {

    data object RetryQueryExchange: ExchangeAction()
    data class Exchange(val id: Int): ExchangeAction()

}