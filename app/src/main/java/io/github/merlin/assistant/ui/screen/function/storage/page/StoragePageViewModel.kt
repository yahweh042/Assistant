package io.github.merlin.assistant.ui.screen.function.storage.page

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.network.service.StorageService
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@AssistedFactory
interface StoragePageViewModelFactory {
    fun create(type: Int): StoragePageViewModel
}

@HiltViewModel(assistedFactory = StoragePageViewModelFactory::class)
class StoragePageViewModel @AssistedInject constructor(
    @Assisted val type: Int,
    val storageService: StorageService
) : AbstractViewModel<StoragePageUiState, StoragePageEvent, StoragePageAction>(
    initialState = run { StoragePageUiState() }
) {

    init {
        viewModelScope.launch {
            view()
        }
    }

    override fun handleAction(action: StoragePageAction) {
        when (action) {
            StoragePageAction.RetryView -> handleRetryView()
            is StoragePageAction.UseGoods -> handleUseGoods(action)
            is StoragePageAction.AbandonGoods -> handleAbandonGoods(action)
        }
    }

    private fun handleAbandonGoods(action: StoragePageAction.AbandonGoods) {

    }

    private fun handleUseGoods(action: StoragePageAction.UseGoods) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("使用中"))
            }
            val batchResponse = storageService.batch(action.goodsInfo.id, action.goodsInfo.num)
            if (batchResponse.result == 0) {
                view()
                sendEvent(StoragePageEvent.ShowToast("使用 ${action.goodsInfo.name}*${action.goodsInfo.num}"))
            } else {
                sendEvent(StoragePageEvent.ShowToast("使用失败 ${batchResponse.msg}"))
            }
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Nothing)
            }
        }
    }

    private fun handleRetryView() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Loading)
            }
            view()
        }
    }

    private suspend fun view() {
        val viewResponse = storageService.view(type)
        if (viewResponse.result == 0) {
            mutableStateFlow.update {
                it.copy(
                    viewState = ViewState.Success(
                        StoragePageUiState.StoragePageContentState(
                            viewResponse.goodsInfos.sortedBy { it.id }
                        )
                    )
                )
            }
        } else {
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Error("${viewResponse.msg}"))
            }
        }
    }
}