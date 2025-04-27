package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.IndexResponse
import javax.inject.Inject

class IndexService @Inject constructor(
    val networkDataSource: NetworkDataSource
) {

    suspend fun index(): IndexResponse {
        val params = mapOf<String, Any>()
        return networkDataSource.request("index", params)
    }

}
