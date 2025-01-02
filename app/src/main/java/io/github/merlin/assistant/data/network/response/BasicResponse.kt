package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse(
    val result: Int,
    val msg: String?,
)
