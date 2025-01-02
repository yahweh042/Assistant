package io.github.merlin.assistant.repo

import io.github.merlin.assistant.data.local.LocalDataSource
import io.github.merlin.assistant.data.local.model.Account
import io.github.merlin.assistant.data.local.model.AccountState
import io.github.merlin.assistant.repo.model.SwitchAccountResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AccountRepo @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val json: Json,
) {

    val accountStateFlow: Flow<AccountState?> =
        localDataSource.accountWrapperFlow.map { data ->
            data?.let { json.decodeFromString(it) }
        }

    private var accountStateCache: AccountState? = null

    val accountState: AccountState?
        get() = accountStateCache ?: runBlocking { accountStateFlow.first().also { accountStateCache = it } }

    suspend fun insertAccount(account: Account) {
        val data = accountState ?: AccountState()
        localDataSource.setAccountWrapper(
            accountWrapper = json.encodeToString(
                data.copy(
                    activeUid = account.uid,
                    accounts = data.accounts.plus(Pair(account.uid, account))
                )
            )
        )
        accountStateCache = null
    }

    suspend fun switchAccount(uid: String): SwitchAccountResult {
        val data = accountState ?: return SwitchAccountResult.NoChange
        if (data.activeUid == uid) {
            return SwitchAccountResult.NoChange
        }
        if (!data.accounts.containsKey(uid)) {
            return SwitchAccountResult.SwitchError("系统错误")
        }
        val newData = data.copy(activeUid = uid)
        localDataSource.setAccountWrapper(
            accountWrapper = json.encodeToString(newData)
        )
        accountStateCache = null
        return SwitchAccountResult.Switched
    }

}