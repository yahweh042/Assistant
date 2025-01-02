package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.JianGeQueryResponse
import javax.inject.Inject

class JianGeService @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun query(isSpecial: Int, group: Int = -1): JianGeQueryResponse {
        val params = mutableMapOf<String, String>()
        params["aid"] = "156"
        params["op"] = "query"
        params["group"] = group.toString()
        params["is_special"] = isSpecial.toString()
        return networkDataSource.request<JianGeQueryResponse>("activity", params)
    }

}