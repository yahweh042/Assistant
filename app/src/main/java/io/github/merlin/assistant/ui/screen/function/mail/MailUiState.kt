package io.github.merlin.assistant.ui.screen.function.mail

import io.github.merlin.assistant.data.network.response.OpenMailResponse
import io.github.merlin.assistant.data.network.response.QueryMailsResponse
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.ViewState

data class MailUiState(
    val type: Int = 2,
    val viewState: ViewState<MailState> = ViewState.Loading,
    val sheetState: SheetState = SheetState.HideSheet,
    val loadingDialogState: LoadingDialogState = LoadingDialogState.Nothing,
) {

    data class MailState(
        val mails: List<QueryMailsResponse.MailItem> = listOf()
    )

    sealed class SheetState {

        data object HideSheet: SheetState()

        data class ShowSheet(val data: OpenMailResponse.MailDetail): SheetState()

    }

}
