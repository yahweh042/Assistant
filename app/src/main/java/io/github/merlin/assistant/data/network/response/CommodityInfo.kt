package io.github.merlin.assistant.data.network.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommodityInfo(
    @SerialName("default_num")
    val defaultNum: Int,
    @SerialName("front_need")
    val frontNeed: Int,
    @SerialName("goods_des")
    val goodsDes: String,
    @SerialName("goods_effect")
    val goodsEffect: String,
    @SerialName("goods_id")
    val goodsId: Int,
    @SerialName("goods_num")
    val goodsNum: Int,
    @SerialName("goods_type")
    val goodsType: Int,
    @SerialName("iconId")
    val iconId: Long,
    @SerialName("id")
    val id: Int,
    @SerialName("limit_type")
    val limitType: Int,
    @SerialName("max_num")
    val maxNum: Int,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("prime_price")
    val primePrice: Int,
    @SerialName("remain")
    val remain: Int,
    @SerialName("seq")
    val seq: Int,
    @SerialName("subtype")
    val subtype: Int,
    @SerialName("vip_level")
    val vipLevel: Int,
)