package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.BasicResponse
import io.github.merlin.assistant.data.network.response.VisitPageResponse
import javax.inject.Inject

class MeridianService @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun visitPage(): VisitPageResponse {
        val params = mapOf(
            "op" to "visitpage"
        )
        return networkDataSource.request("meridian", params)
    }

    suspend fun visit(id: Int = 0): VisitPageResponse {
        val params = mapOf(
            "op" to "visit",
            "id" to id,
        )
        return networkDataSource.request("meridian", params)
    }

    suspend fun award(index: Int): BasicResponse {
        val params = mapOf(
            "op" to "visit",
            "index" to index,
        )
        return networkDataSource.request("meridian", params)
    }

    suspend fun upgrade(soulId: Int): BasicResponse {
        val params = mapOf(
            "op" to "upgrade",
            "soulid" to soulId,
        )
        return networkDataSource.request("meridian", params)
    }

}
