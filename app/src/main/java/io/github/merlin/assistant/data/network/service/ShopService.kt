package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.ShopResponse
import javax.inject.Inject

class ShopService @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun viewShop(type: String): ShopResponse {
        val params = mutableMapOf<String, String>()
        params["shoptype"] = type
        return networkDataSource.request("shop", params)
    }

}