package io.github.merlin.assistant.repo

import io.github.merlin.assistant.data.local.LocalDataSource
import io.github.merlin.assistant.data.local.LocalShopTypeProvider
import io.github.merlin.assistant.data.network.response.ShopResponse
import io.github.merlin.assistant.data.network.response.toStorageGoodsNum
import io.github.merlin.assistant.data.network.service.ShopService
import io.github.merlin.assistant.data.network.service.StorageService
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ShopRepo @Inject constructor(
    private val shopService: ShopService,
    private val storageService: StorageService,
    private val localDataSource: LocalDataSource,
    private val json: Json,
) {

    val shopTypesFlow = combine(
        LocalShopTypeProvider.shopTypesFlow,
        localDataSource.showShopTypesFlow
    ) { shopTypes, showShopTypes ->
        showShopTypes?.let {
            val set = json.decodeFromString<Set<String>>(it)
            return@combine shopTypes.map { shopType ->
                shopType.copy(
                    show = set.contains(shopType.type)
                )
            }
        }
        return@combine shopTypes
    }


    suspend fun viewShop(type: String): ShopResponse {
        return shopService.viewShop(type)
    }

    suspend fun storageGoodsNum(): Map<Int, Int> {
        // 1 药水 2 强化 3 其它
        val goodsNum = HashMap<Int, Int>()
        for (type in listOf(1, 2, 3)) {
            val tmpGoodsNum = storageService.view(type).toStorageGoodsNum()
            goodsNum.putAll(tmpGoodsNum)
        }
        return goodsNum
    }

    suspend fun buy(id: Int, subtype: Int, num: Int, price: Int) =
        shopService.buy(id, subtype, num, price)
}