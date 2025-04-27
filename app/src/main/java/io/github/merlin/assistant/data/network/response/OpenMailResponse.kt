package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMailResponse(
    val result: Int,
    val msg: String?,
    val id: Int,
    val reddot: Int,
    @SerialName("maillist")
    val mailDetail: MailDetail,
) {

    @Serializable
    data class MailDetail(
        val id: String,
        val title: String,
        val content: String,
        val click: Int,
        val sender: String,
        val status: Int,
        val reward: Int,
        @SerialName("rewardlist")
        val rewardList: List<Reward>,
    ) {

        @Serializable
        data class Reward(
            val id:Int,
            val num:Int,
            @SerialName("icon_id")
            val iconId: Long?,
        )

    }


}
