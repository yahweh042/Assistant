package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class LevelChallengeResponse(
    val result: Int,
    val msg: String?,
    val win: Int?,
)
