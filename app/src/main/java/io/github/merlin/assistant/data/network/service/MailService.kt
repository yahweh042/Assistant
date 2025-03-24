package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.MailResponse
import javax.inject.Inject

class MailService @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun getMails(type: Int): MailResponse {
        val params = mapOf(
            "op" to "mailist",
            "needreload" to 1,
            "type" to type
        )
        return networkDataSource.request<MailResponse>("mail", params)
    }

}