package io.github.merlin.assistant.ui.screen.function.shop.page

import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.repo.ShopRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.ViewState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@AssistedFactory
interface ShopPageViewModelFactory {
    fun create(type: String): ShopPageViewModel
}

@HiltViewModel(assistedFactory = ShopPageViewModelFactory::class)
class ShopPageViewModel @AssistedInject constructor(
    @Assisted val type: String,
    private val shopRepo: ShopRepo
) : AbstractViewModel<ShopPageUiState, ShopPageEvent, ShopPageAction>(
    initialState = run {
        ShopPageUiState()
    }
) {

    init {
        viewModelScope.launch {
            viewShop(type)
        }
    }

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
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Loading("购买中"))
            }
            val goods = action.commodityInfo
            val response = shopRepo.buy(
                id = goods.id,
                subtype = goods.goodsType,
                num = action.num,
                price = goods.price,
            )
            if (response.result == 0) {
                viewShop(action.shopType)
                sendEvent(ShopPageEvent.ShowToast("购买成功 ${action.commodityInfo.name}*${action.num}"))
                mutableStateFlow.update { it.copy(dialogState = ShopPageUiState.CommodityInfoDialogState.Hide) }
            } else {
                sendEvent(ShopPageEvent.ShowToast("${response.msg}"))
            }
            mutableStateFlow.update {
                it.copy(loadingDialogState = LoadingDialogState.Nothing)
            }
        }
    }

    private fun handleUpdateGoodsNum(action: ShopPageAction.UpdateGoodsNum) {
        viewModelScope.launch {
            val commodityInfo = action.commodityInfo
            val num = when {
                action.num < 1 -> 1
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
                    dialogState = ShopPageUiState.CommodityInfoDialogState.Show(action.commodityInfo)
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
            mutableStateFlow.update {
                it.copy(viewState = ViewState.Loading)
            }
            viewShop(action.shopType)
        }
    }

    private suspend fun viewShop(shopType: String) {
        val storageGoodsInfo = shopRepo.storageGoodsNum()
        val shopResponse = shopRepo.viewShop(shopType)
        if (shopResponse.result == 0) {
            val money = shopResponse.money(shopType, storageGoodsInfo) ?: 0
            val commodityInfo = shopResponse.commodityInfo?.map {
                it.copy(storageNum = storageGoodsInfo[it.goodsId] ?: 0)
            } ?: listOf()
            mutableStateFlow.update {
                it.copy(
                    viewState = ViewState.Success(
                        ShopPageUiState.ShopPageState(
                            money = money,
                            commodityInfo = commodityInfo
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