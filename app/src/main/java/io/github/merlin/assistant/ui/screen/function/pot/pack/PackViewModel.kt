package io.github.merlin.assistant.ui.screen.function.pot.pack

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.network.service.StorageService
import io.github.merlin.assistant.repo.PotRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackViewModel @Inject constructor(
    val potRepo: PotRepo,
    val storageService: StorageService,
) : AbstractViewModel<PackUiState, PackEvent, PackAction>(
    initialState = run {
        PackUiState()
    }
) {

    init {
        viewModelScope.launch {
            viewPack()
        }
    }

    private suspend fun viewPack() {
        val viewPackResponse = potRepo.viewPack()
        if (viewPackResponse.result == 0 && viewPackResponse.goodsInfo?.isNotEmpty() == true) {
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Success(viewPackResponse.goodsInfo.sortedBy { it.id }))
            }
        } else {
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Error("${viewPackResponse.msg}"))
            }
        }
    }

    override fun handleAction(action: PackAction) {
        when (action) {
            PackAction.RetryViewPack -> handleRetryViewPack()
            is PackAction.UseGoods -> handleUseGoods(action)
            PackAction.PullToRefresh -> handlePullToRefresh()
        }
    }

    private fun handlePullToRefresh() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(isRefreshing = true)
            }
            viewPack()
            mutableStateFlow.update {
                it.copy(isRefreshing = false)
            }
        }
    }

    private fun handleRetryViewPack() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Loading)
            }
            viewPack()
        }
    }

    private fun handleUseGoods(action: PackAction.UseGoods) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("使用中"))
            }
            val useResponse = storageService.use(action.goodsInfo.id)
            if (useResponse.result == 0) {
                viewPack()
                sendEvent(PackEvent.ShowToast("使用成功"))
            } else {
                sendEvent(PackEvent.ShowToast("使用失败 ${useResponse.msg}"))
            }
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Nothing)
            }
        }
    }

}