package io.github.merlin.assistant.data.network.response

import io.github.merlin.assistant.data.local.model.Account
import kotlinx.serialization.Serializable
import java.net.URLDecoder

@Serializable
data class LoginResponse(
    val result: Int,
    val msg: String,
    val name: String? = null,
    val headimgurl: String? = null,
    val uid: String? = null,
    val openid: String? = null,
    val token: String? = null,
)

fun LoginResponse.toAccount(): Account {
    return Account(
        uid = uid ?: "",
        name = URLDecoder.decode((name ?: ""), "utf-8"),
        headImg = (headimgurl ?: "").replace("http://", "https://"),
        token = token ?: "",
        openid = openid ?: "",
        isActive = false,
    )
}
