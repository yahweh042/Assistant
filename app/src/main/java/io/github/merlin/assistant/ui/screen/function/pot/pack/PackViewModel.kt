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
        viewPack()
    }

    private fun viewPack() {
        viewModelScope.launch {
            val viewPackResponse = potRepo.viewPack()
            if (viewPackResponse.result == 0 && viewPackResponse.goodsInfo?.isNotEmpty() == true) {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Success(viewPackResponse.goodsInfo))
                }
            } else {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Error("${viewPackResponse.msg}"))
                }
            }
        }
    }

    override fun handleAction(action: PackAction) {
        when (action) {
            is PackAction.UseGoods -> handleUseGoods(action)
        }
    }

    private fun handleUseGoods(action: PackAction.UseGoods) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("使用中"))
            }
            storageService.use(action.goodsInfo.id)
        }
    }

}