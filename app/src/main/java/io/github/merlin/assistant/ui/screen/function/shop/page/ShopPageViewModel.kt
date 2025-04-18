package io.github.merlin.assistant.ui.screen.function.shop.page

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.repo.ShopRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopPageViewModel @Inject constructor(
    private val shopRepo: ShopRepo,
    private val stateHandle: SavedStateHandle
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
            is ShopPageAction.UpdateGoodsNum -> handleUpdateGoodsNum(action)
            is ShopPageAction.BuyGoods -> handleBuyGoods(action)
        }
    }

    private fun handleBuyGoods(action: ShopPageAction.BuyGoods) {
        viewModelScope.launch {
            val goods = action.commodityInfo
            val response = shopRepo.buy(
                id = goods.id,
                subtype = goods.goodsType,
                num = action.num,
                price = goods.price,
            )
            if (response.result == 0) {
                sendEvent(ShopPageEvent.ShowToast("购买成功 ${action.commodityInfo.name}*${action.num}"))
                val shopType = action.shopType
                handleRefreshShop(ShopPageAction.RefreshShop(shopType), showLoading = false)
                mutableStateFlow.update { it.copy(dialogState = ShopPageUiState.CommodityInfoDialogState.Hide) }
            } else {
                sendEvent(ShopPageEvent.ShowToast("${response.msg}"))
            }
        }
    }

    private fun handleUpdateGoodsNum(action: ShopPageAction.UpdateGoodsNum) {
        viewModelScope.launch {
            val commodityInfo = action.commodityInfo
            val num = when {
                action.num < 0 -> 0
                action.num > commodityInfo.maxNum -> commodityInfo.maxNum
                else -> action.num
            }

            mutableStateFlow.update {
                it.copy(
                    dialogState = ShopPageUiState.CommodityInfoDialogState.Show(
                        commodityInfo = action.commodityInfo,
                        num = num,
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

    private fun handleRefreshShop(action: ShopPageAction.RefreshShop, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                mutableStateFlow.update { it.copy(viewState = ViewState.Loading) }
            }
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