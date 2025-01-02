package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.response.FightResponse
import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.MainViewResponse
import javax.inject.Inject

class JewelService @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun mainView(areaId: String): MainViewResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "main_view"
        params["area_id"] = areaId
        return networkDataSource.request("jewel_war", params)
    }

    suspend fun fight(
        areaId: String,
        areaPosition: String,
        chooseSecretReel: String = "0|0|0|0"
    ): FightResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "fight"
        params["area_id"] = areaId
        params["area_position"] = areaPosition
        params["choose_secret_reel"] = chooseSecretReel
        return networkDataSource.request("jewel_war", params)
    }

    suspend fun jewelEnd(
        areaId: String,
        areaPosition: String,
    ): FightResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "jewel_end"
        params["area_id"] = areaId
        params["area_position"] = areaPosition
        return networkDataSource.request("jewel_war", params)
    }

    suspend fun buyVit(): FightResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "buy_vit"
        return networkDataSource.request("jewel_war", params)
    }


}