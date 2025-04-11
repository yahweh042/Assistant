package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

@Serializable
data class QueryMailsResponse(
    val result: Int,
    val msg: String?,
    @SerialName("maillist")
    val mails: List<MailItem> = listOf(),
) {

    @Serializable
    data class MailItem(
        val id: String,
        val type: Int,
        val title: String,
        val sender: String,
        val status: Int,
        val time: String,
        val click: Int,
        @SerialName("expire_time")
        val expireTime: String,
        val reward: Int,
    ) {

        val formatTime: String
            get() = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(time.toLong() * 1000)

    }
}
