package io.github.merlin.assistant.ui.screen.function.mail

import io.github.merlin.assistant.data.network.response.MailResponse
import io.github.merlin.assistant.ui.base.ViewState

data class MailUiState(
    val type: Int = 2,
    val viewState: ViewState = ViewState.Loading,
) {

    data class MailState(
        val mails: List<MailResponse.MailItem> = listOf()
    )

}
