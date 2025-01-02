package io.github.merlin.assistant.repo

import io.github.merlin.assistant.data.network.response.ShopResponse
import io.github.merlin.assistant.data.network.service.ShopService
import javax.inject.Inject

class ShopRepo @Inject constructor(
    private val shopService: ShopService,
) {

    suspend fun viewShop(type: String): ShopResponse {
        return shopService.viewShop(type)
    }
}