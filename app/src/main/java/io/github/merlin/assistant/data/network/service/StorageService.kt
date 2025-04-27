package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.BasicResponse
import javax.inject.Inject

class StorageService @Inject constructor(
    val networkDataSource: NetworkDataSource
) {

    suspend fun use(id: Int): BasicResponse {
        val params = mapOf(
            "op" to "use",
            "id" to id,
        )
        return networkDataSource.request<BasicResponse>("storage", params)
    }

}