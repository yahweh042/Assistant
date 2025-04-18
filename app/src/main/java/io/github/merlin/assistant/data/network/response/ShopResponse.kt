package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShopResponse(
    @SerialName("result")
    val result: Int,
    @SerialName("msg")
    val msg: String?,
    @SerialName("commodity_info")
    val commodityInfo: List<CommodityInfo>?,
)