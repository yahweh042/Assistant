package io.github.merlin.assistant.ui.screen.function.shop.page

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.repo.ShopRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopPageViewModel @Inject constructor(
    private val shopRepo: ShopRepo
) : AbstractViewModel<ShopPageUiState, ShopPageEvent, ShopPageAction>(
    initialState = run {
        ShopPageUiState()
    }
) {

    override fun handleAction(action: ShopPageAction) {
        when (action) {
            is ShopPageAction.RefreshShop -> handleRefreshShop(action)
            ShopPageAction.HideDialog -> handleHideDialog(action)
            is ShopPageAction.ShowDialog -> handleShowDialog(action)
            is ShopPageAction.UpdateSlidePosition -> handleUpdateSlidePosition(action)
        }
    }

    private fun handleUpdateSlidePosition(action: ShopPageAction.UpdateSlidePosition) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ShopPageUiState.CommodityInfoDialogState.Show(
                        commodityInfo = action.commodityInfo,
                        sliderPosition = action.sidePosition,
                    )
                )
            }
        }
    }

    private fun handleShowDialog(action: ShopPageAction.ShowDialog) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ShopPageUiState.CommodityInfoDialogState.Show(
                        action.commodityInfo
                    )
                )
            }
        }
    }

    private fun handleHideDialog(action: ShopPageAction) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(dialogState = ShopPageUiState.CommodityInfoDialogState.Hide) }
        }
    }

    private fun handleRefreshShop(action: ShopPageAction.RefreshShop) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(viewState = ViewState.Loading) }
            val shopResponse = shopRepo.viewShop(action.shopType)
            if (shopResponse.result == 0) {
                mutableStateFlow.update {
                    it.copy(
                        viewState = ViewState.Success(
                            ShopPageUiState.ContentState(
                                shopResponse.commodityInfo ?: listOf()
                            )
                        )
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(viewState = ViewState.Error(shopResponse.msg ?: ""))
                }
            }
        }
    }

}