package io.github.merlin.assistant.data.network.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoodsInfo(
    @SerialName("canabandon")
    val canAbandon: Int,
    @SerialName("canconv")
    val canConv: Int,
    @SerialName("canuse")
    val canUse: Int,
    @SerialName("canusebatch")
    val canUseBatch: Int,
    @SerialName("confirm_msg")
    val confirmMsg: String,
    @SerialName("effect_desc")
    val effectDesc: String,
    @SerialName("goods_desc")
    val goodsDesc: String,
    @SerialName("goods_type")
    val goodsType: Int,
    @SerialName("goods_validity")
    val goodsValidity: String,
    @SerialName("icon_id")
    val iconId: Long,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("num")
    val num: Int,
    @SerialName("short_des")
    val shortDes: String,
    @SerialName("subid")
    val subId: Int,
    @SerialName("target_url")
    val targetUrl: String,
    @SerialName("type")
    val type: Int
)