package io.github.merlin.assistant.ui.screen.function.shop

import io.github.merlin.assistant.data.local.model.ShopType

data class ShopUiState(
    val shopTypes: List<ShopType> = listOf()
)