package io.github.merlin.assistant.data.network.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangedItem(
    @SerialName("canabandon")
    val canabandon: Int,
    @SerialName("canconv")
    val canconv: Int,
    @SerialName("canuse")
    val canuse: Int,
    @SerialName("canusebatch")
    val canusebatch: Int,
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
    val iconId: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("num")
    val num: Int,
    @SerialName("short_des")
    val shortDes: String,
    @SerialName("subid")
    val subid: Int,
    @SerialName("target_url")
    val targetUrl: String,
    @SerialName("type")
    val type: Int
)