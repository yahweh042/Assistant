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

    @SerialName("beasts_book_point1")
    val beastsBookPoint1: Int?,
    @SerialName("beasts_book_point2")
    val beastsBookPoint2: Int?,
    @SerialName("cave_point")
    val cavePoint: Int?,
    @SerialName("chrono_card_num")
    val chronoCardNum: Int?,
    @SerialName("doudou")
    val doudou: Int?,
    @SerialName("doushen_medal")
    val doushenMedal: Int?,
    @SerialName("fragment")
    val fragment: Int?,
    @SerialName("gzwulin_score")
    val gzwulinScore: Int?,
    @SerialName("honor")
    val honor: String?,
    @SerialName("internal_force")
    val internalForce: Int?,
    @SerialName("jewel_num")
    val jewelNum: Int?,
    @SerialName("kAct147Point")
    val kAct147Point: Int?,
    @SerialName("kAct195Point")
    val kAct195Point: Int?,
    @SerialName("king_jingpo_num")
    val kingJingpoNum: Int?,
    @SerialName("king_medal")
    val kingMedal: Int?,
    @SerialName("pot_world_gold")
    val potWorldGold: Int?,
    @SerialName("servant_cash")
    val servantCash: Int?,
    @SerialName("shen_bing_coin")
    val shenBingCoin: Int?,
    @SerialName("skin_point")
    val skinPoint: Int?,
    @SerialName("sx_point")
    val sxPoint: Int?,
    @SerialName("sx_point2")
    val sxPoint2: Int?,
    @SerialName("winpoint")
    val winPoint: Int?,
) {
    
    fun money(type: String): Int?  = when(type) {
        "1" -> doudou
        "3" -> winPoint
        "4" -> servantCash
        "5" -> cavePoint
        "6" -> kingMedal
        "8" -> doushenMedal
        "41" -> skinPoint
        "53" -> potWorldGold
        else -> 0
    }
    
}