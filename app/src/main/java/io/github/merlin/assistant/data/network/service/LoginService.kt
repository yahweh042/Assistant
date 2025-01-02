package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.LoginResponse
import javax.inject.Inject

class LoginService @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun login(token: String, openid: String): LoginResponse {
        val params = mutableMapOf<String, String>()
        params["h5token"] = token
        params["h5openid"] = openid
        return networkDataSource.request<LoginResponse>("index", params)
    }

}