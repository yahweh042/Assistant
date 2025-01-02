package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FightResponse(
    val result: Int,
    val msg: String?,
    @SerialName("is_win")
    val isWin: Int? = null,
)