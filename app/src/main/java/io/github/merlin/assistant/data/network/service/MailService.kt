package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.BasicResponse
import io.github.merlin.assistant.data.network.response.OpenMailResponse
import io.github.merlin.assistant.data.network.response.QueryMailsResponse
import javax.inject.Inject

class MailService @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun queryMails(type: Int): QueryMailsResponse {
        val params = mapOf(
            "op" to "mailist",
            "needreload" to 1,
            "type" to type
        )
        return networkDataSource.request<QueryMailsResponse>("mail", params)
    }

    suspend fun openMail(id: Int): OpenMailResponse {
        val params = mapOf(
            "op" to "getmail",
            "id" to id,
        )
        return networkDataSource.request<OpenMailResponse>("mail", params)
    }

    suspend fun getReward(id: Int): BasicResponse {
        val params = mapOf(
            "op" to "getreward",
            "id" to id,
            "needreload" to 1,
        )
        return networkDataSource.request<BasicResponse>("mail", params)
    }

    suspend fun delMail(id: Int): BasicResponse {
        val params = mapOf(
            "op" to "delmail",
            "id" to id,
        )
        return networkDataSource.request<BasicResponse>("mail", params)
    }

    suspend fun batchDelMail(ids: List<Int>): BasicResponse {
        val params = mapOf(
            "op" to "batchdelmail",
            "ids" to ids.joinToString("|"),
        )
        return networkDataSource.request<BasicResponse>("mail", params)
    }

}