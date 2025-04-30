package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StorageViewResponse(
    val result: Int,
    val msg: String?,
    @SerialName("goods_info")
    val goodsInfos: List<GoodsInfo> = listOf(),
)

fun StorageViewResponse.toStorageGoodsNum(): Map<Int, Int> {
    return if (result == 0) {
        goodsInfos.associate { it.id to it.num }
    } else {
        mapOf()
    }
}