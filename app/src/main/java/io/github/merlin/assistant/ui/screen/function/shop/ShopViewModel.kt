package io.github.merlin.assistant.ui.screen.function.shop

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.merlin.assistant.data.local.model.ShopType
import io.github.merlin.assistant.repo.ShopRepo
import io.github.merlin.assistant.ui.base.AbstractViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepo: ShopRepo
) : AbstractViewModel<ShopUiState, ShopEvent, ShopAction>(
    initialState = run {
        ShopUiState()
    }
) {

    init {
        shopRepo.shopTypesFlow
            .onEach { receiveShopTypesUpdate(it) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: ShopAction) {
        when (action) {
            is ShopAction.SelectedTabIndexChange -> handleSelectedTabIndexChange(action)
        }
    }

    private fun handleSelectedTabIndexChange(action: ShopAction.SelectedTabIndexChange) {
        viewModelScope.launch {

        }
    }

    private fun receiveShopTypesUpdate(shopTypes: List<ShopType>) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(shopTypes = shopTypes) }
        }
    }


}