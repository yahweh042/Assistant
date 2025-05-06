package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.Serializable

@Serializable
open class BasicResponse(
    var result: Int = 0,
    var msg: String? = null,
)

data class LoginFailResponse(
    val name: String,
) : BasicResponse()
