package io.github.merlin.assistant.ui.screen.function.storage.page

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.network.response.GoodsInfo
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
            is StoragePageAction.ExchangeGoods -> handleExchangeGoods(action)
        }
    }

    private fun handleExchangeGoods(action: StoragePageAction.ExchangeGoods) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("正在兑换"))
            }
            val goodsInfo = action.goodsInfo
            val exId = goodsInfo.targetUrl.split("|")[1].toInt()
            val exchangeResponse = storageService.exchange(exId, goodsInfo.num)
            if (exchangeResponse.result == 0) {
                view()
                sendEvent(StoragePageEvent.ShowToast("兑换 ${goodsInfo.name}*${goodsInfo.num}"))
            } else {
                sendEvent(StoragePageEvent.ShowToast("兑换失败 ${exchangeResponse.msg}"))
            }
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Nothing)
            }
        }
    }

    private fun handleAbandonGoods(action: StoragePageAction.AbandonGoods) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("正在丢弃"))
            }
            val goodsInfo = action.goodsInfo
            val abandonResponse = storageService.abandon(goodsInfo.id)
            if (abandonResponse.result == 0) {
                view()
                sendEvent(StoragePageEvent.ShowToast("丢弃 ${goodsInfo.name}*${goodsInfo.num}"))
            } else {
                sendEvent(StoragePageEvent.ShowToast("丢弃失败 ${abandonResponse.msg}"))
            }
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Nothing)
            }
        }
    }

    private fun handleUseGoods(action: StoragePageAction.UseGoods) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("正在使用"))
            }
            val goodsInfo = action.goodsInfo
            when {
                goodsInfo.canUseBatch == 1 -> batchUseGoods(goodsInfo, goodsInfo.num)
                goodsInfo.canUse == 1 -> singleUseGoods(goodsInfo)
                else -> sendEvent(StoragePageEvent.ShowToast("使用失败 不支持使用"))
            }
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Nothing)
            }
        }
    }

    private suspend fun singleUseGoods(goodsInfo: GoodsInfo) {
        val useResponse = storageService.use(goodsInfo.id)
        if (useResponse.result == 0) {
            view()
            sendEvent(StoragePageEvent.ShowToast("使用 ${goodsInfo.name}"))
        } else {
            sendEvent(StoragePageEvent.ShowToast("使用失败 ${useResponse.msg}"))
        }
    }

    private suspend fun batchUseGoods(goodsInfo: GoodsInfo, num: Int) {
        val batchResponse = storageService.batch(goodsInfo.id, goodsInfo.num)
        if (batchResponse.result == 0) {
            view()
            sendEvent(StoragePageEvent.ShowToast("使用 ${goodsInfo.name}*${goodsInfo.num}"))
        } else {
            sendEvent(StoragePageEvent.ShowToast("使用失败 ${batchResponse.msg}"))
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