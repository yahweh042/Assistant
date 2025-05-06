package io.github.merlin.assistant.ui.screen.function.mail

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.network.service.MailService
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.LoadingDialogState
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
        viewModelScope.launch {
            queryMails()
        }
    }

    private suspend fun queryMails(type: Int = state.type) {
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

    override fun handleAction(action: MailAction) {
        when (action) {
            is MailAction.QueryMails -> handleQueryMails(action)
            MailAction.RetryQueryMails -> handleRetryQueryMails()
            is MailAction.OpenMail -> openMail(action.id)
            is MailAction.GetReward -> getReward(action.id)
            MailAction.HideSheet -> hideSheet()
        }
    }

    private fun handleQueryMails(action: MailAction.QueryMails) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(type = action.type)
            }
            queryMails(action.type)
        }
    }

    private fun handleRetryQueryMails() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Loading)
            }
            queryMails()
        }
    }

    private fun getReward(id: Int) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("领取奖励"))
            }
            val response = mailService.getReward(id)
            if (response.result == 0) {
                queryMails()
                mutableStateFlow.update {
                    it.copy(
                        loadingDialogState = LoadingDialogState.Nothing,
                        sheetState = MailUiState.SheetState.HideSheet,
                    )
                }
                sendEvent(MailEvent.ShowToast("领取奖励 OK"))
            } else {
                mutableStateFlow.update {
                    it.copy(
                        loadingDialogState = LoadingDialogState.Nothing,
                    )
                }
                sendEvent(MailEvent.ShowToast("领取奖励 失败 ${response.msg}"))
            }
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
            mutableStateFlow.update { it.copy(loadingDialogState = LoadingDialogState.Loading("打开邮件")) }
            val openMailResponse = mailService.openMail(id)
            mutableStateFlow.update { it.copy(loadingDialogState = LoadingDialogState.Nothing) }
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