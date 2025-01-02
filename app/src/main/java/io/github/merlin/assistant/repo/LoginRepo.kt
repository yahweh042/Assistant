package io.github.merlin.assistant.repo

import io.github.merlin.assistant.data.network.service.LoginService
import io.github.merlin.assistant.data.network.response.toAccount
import io.github.merlin.assistant.repo.model.LoginResult
import javax.inject.Inject

class LoginRepo @Inject constructor(
    private val accountRepo: AccountRepo,
    private val loginService: LoginService,
) {

    suspend fun loginWithCookie(cookie: String): LoginResult {
        if (cookie.isBlank()) {
            return LoginResult.Error("登陆失败,请重新登录")
        }
        val map = cookie.split(";")
            .map { it.trim().split("=") }
            .associate { it[0] to it.getOrElse(1) { "" } }
        val h5token = map.getOrDefault("h5game_accesstoken", "")
        val h5openid = map.getOrDefault("h5game_openid", "")
        if (h5token.isBlank() or h5openid.isBlank()) {
            return LoginResult.Error("登陆失败,请重新登录")
        }
        val loginResponse = loginService.login(h5token, h5openid)
        if (loginResponse.result == 0) {
            accountRepo.insertAccount(loginResponse.toAccount())
            return LoginResult.Success
        } else {
            return LoginResult.Error("登陆失败,${loginResponse.msg}")
        }
    }

}