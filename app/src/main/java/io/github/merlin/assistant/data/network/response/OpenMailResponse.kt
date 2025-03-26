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
    )


}
