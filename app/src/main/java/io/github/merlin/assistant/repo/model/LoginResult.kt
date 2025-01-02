package io.github.merlin.assistant.repo.model

sealed class LoginResult {

    data object Success: LoginResult()
    data class Error(val msg: String): LoginResult()

}