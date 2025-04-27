package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IndexResponse(
    val result: Int,
    val msg: String?,
    @SerialName("goods_info")
    val goodsInfo: List<GoodsInfo> = listOf(),
)