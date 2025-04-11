package io.github.merlin.assistant.ui.screen.function.mail

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.network.service.MailService
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MailViewModel @Inject constructor(
    private val mailService: MailService
) : AbstractViewModel<MailUiState, MailEvent, MailAction>(
    initialState = run {
        MailUiState()
    }
) {
    init {
        queryMails()
    }

    private fun queryMails(type: Int = state.type) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Loading, type = type)
            }
            val mailResponse = mailService.queryMails(type)
            if (mailResponse.result == 0) {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Success(MailUiState.MailState(mailResponse.mails.reversed())))
                }
            } else {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Error(mailResponse.msg ?: "未知错误"))
                }
            }
        }
    }

    override fun handleAction(action: MailAction) {
        when (action) {
            is MailAction.QueryMails -> queryMails(action.type)
            is MailAction.OpenMail -> openMail(action.id)
            MailAction.HideSheet -> hideSheet()
        }
    }

    private fun hideSheet() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(sheetState = MailUiState.SheetState.HideSheet)
            }
        }
    }

    private fun openMail(id: Int) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(operateLoading = true) }
            val openMailResponse = mailService.openMail(id)
            mutableStateFlow.update { it.copy(operateLoading = false) }
            if (openMailResponse.result == 0) {
                mutableStateFlow.update {
                    it.copy(sheetState = MailUiState.SheetState.ShowSheet(openMailResponse.mailDetail))
                }
            } else {
                sendEvent(MailEvent.ShowToast("${openMailResponse.msg}"))
            }
        }
    }

}