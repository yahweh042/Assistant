package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryArenaResponse(
    val result: Int,
    val msg: String?,
    @SerialName("free_times")
    val freeTimes: Int = 0,
    @SerialName("self_rank")
    val selfRank: Int = 0,
    @SerialName("total_point")
    val totalPoint: Int = 0,
    @SerialName("oppinfo")
    val oppInfo: List<OppInfo> = listOf(),
) {

    @Serializable
    data class OppInfo(
        val uid: String,
        val name: String,
        val sex: Int,
        val level: Int,
        val rank: Int,
        @SerialName("headimgurl")
        val headImgUrl: String,
        @SerialName("zone_name")
        val zoneName: String,
        @SerialName("attack_power")
        val attackPower: Int,
        @SerialName("vip_level")
        val vipLevel: Int,
        @SerialName("picture_frame_artid")
        val pictureFrameArtId: String,
    )

}
