package io.github.merlin.assistant.data.local

import io.github.merlin.assistant.data.local.model.ShopType
import kotlinx.coroutines.flow.flow

object LocalShopTypeProvider {

    val shopTypesFlow = flow {
        emit(
            listOf(
                ShopType(type = "3", name = "胜点"),
                ShopType(type = "4", name = "家财"),
                ShopType(type = "5", name = "洞穴"),
                ShopType(type = "6", name = "王者"),
                ShopType(type = "8", name = "斗神"),
            )
        )
    }

}