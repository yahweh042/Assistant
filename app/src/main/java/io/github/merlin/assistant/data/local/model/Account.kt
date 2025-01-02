package io.github.merlin.assistant.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val uid: String,
    val name: String,
    val headImg: String,
    val token: String,
    val openid: String,
    val isActive: Boolean? = false,
)

@Serializable
data class AccountState(
    val activeUid: String? = null,
    val accounts: Map<String, Account> = hashMapOf(),
) {

    val activeAccount: Account?
        get() = accounts[activeUid]
}