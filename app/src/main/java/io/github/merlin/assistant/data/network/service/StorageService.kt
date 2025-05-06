package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.BasicResponse
import io.github.merlin.assistant.data.network.response.StorageViewResponse
import javax.inject.Inject

class StorageService @Inject constructor(
    val networkDataSource: NetworkDataSource
) {

    suspend fun view(type: Int): StorageViewResponse {
        val params = mapOf(
            "op" to "view",
            "type" to type,
        )
        return networkDataSource.request<StorageViewResponse>("storage", params)
    }

    suspend fun use(id: Int): BasicResponse {
        val params = mapOf(
            "op" to "use",
            "id" to id,
        )
        return networkDataSource.request<BasicResponse>("storage", params)
    }

    suspend fun batch(id: Int, num: Int): BasicResponse {
        val params = mapOf(
            "op" to "batch",
            "id" to id,
            "num" to num,
        )
        return networkDataSource.request<BasicResponse>("storage", params)
    }

}