package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainViewResponse(
    val result: Int,
    val msg: String? = "",
    @SerialName("jewel_list")
    val jewelList: List<Jewel> = listOf(),
) {

    @Serializable
    data class Jewel(
        val level: Int,
        @SerialName("role_name")
        val roleName: String? = null,
        @SerialName("fac_name")
        val facName: String? = null,
        @SerialName("is_me")
        val isMe: Int,
        val status: Int,
    )
}
