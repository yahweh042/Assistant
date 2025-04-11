package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class FightArenaResponse(
    val result: Int,
    val msg: String?,
    val win: Int = 0,
)