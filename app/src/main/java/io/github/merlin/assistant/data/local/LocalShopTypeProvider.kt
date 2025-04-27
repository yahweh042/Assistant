package io.github.merlin.assistant.data.local

import io.github.merlin.assistant.data.local.model.ShopType
import kotlinx.coroutines.flow.flow

object LocalShopTypeProvider {

    val shopTypesFlow = flow {
        emit(
            listOf(
                ShopType(type = "1", name = "杂货铺"),
                ShopType(type = "3", name = "胜点"),
                ShopType(type = "4", name = "家财"),
                ShopType(type = "5", name = "洞穴"),
                ShopType(type = "6", name = "王者"),
                ShopType(type = "8", name = "斗神"),
                ShopType(type = "41", name = "替身"),
                ShopType(type = "16", name = "圣物"),
                ShopType(type = "17", name = "高级精华"),
                ShopType(type = "25", name = "特级精华"),
            )
        )
    }

}