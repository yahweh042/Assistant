package io.github.merlin.assistant.data.network.service

import io.github.merlin.assistant.data.network.NetworkDataSource
import io.github.merlin.assistant.data.network.response.BasicResponse
import io.github.merlin.assistant.data.network.response.FightArenaResponse
import io.github.merlin.assistant.data.network.response.GetAwardResponse
import io.github.merlin.assistant.data.network.response.MysteryResponse
import io.github.merlin.assistant.data.network.response.PotResponse
import io.github.merlin.assistant.data.network.response.QueryArenaResponse
import io.github.merlin.assistant.data.network.response.QueryExchangeResponse
import io.github.merlin.assistant.data.network.response.ViewPackResponse
import javax.inject.Inject

class PotService @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun index(): PotResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "index"
        return networkDataSource.request<PotResponse>("pot_world", params)
    }

    suspend fun getAward(type: String): GetAwardResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "get_award"
        params["type"] = type
        return networkDataSource.request<GetAwardResponse>("pot_world", params)
    }

    suspend fun challengeLevel(levelId: String): PotResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "challenge_level"
        params["level_id"] = levelId
        return networkDataSource.request<PotResponse>("pot_world", params)
    }

    suspend fun challengeBoss(bossId: String): PotResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "challenge_boss"
        params["boss_id"] = bossId
        return networkDataSource.request<PotResponse>("pot_world", params)
    }

    suspend fun adventure(): PotResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "adventure"
        return networkDataSource.request<PotResponse>("pot_world", params)
    }

    suspend fun equip(equipmentId: String): PotResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "equip"
        params["equipment_id"] = equipmentId
        return networkDataSource.request<PotResponse>("pot_world", params)
    }

    suspend fun decompose(equipmentId: Int): PotResponse {
        val params = mutableMapOf<String, Any>()
        params["op"] = "decompose"
        params["equipment_id"] = equipmentId
        return networkDataSource.request<PotResponse>("pot_world", params)
    }

    suspend fun queryLevel(group: String): BasicResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "query_level"
        params["group"] = group
        return networkDataSource.request<BasicResponse>("pot_world", params)
    }

    suspend fun queryBoss(group: String): BasicResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "query_boss"
        params["group"] = group
        return networkDataSource.request<BasicResponse>("pot_world", params)
    }

    suspend fun upgradeSlot(type: String): PotResponse {
        val params = mutableMapOf<String, String>()
        params["op"] = "upgrade_slot"
        params["type"] = type
        return networkDataSource.request<PotResponse>("pot_world", params)
    }

    suspend fun queryMystery(): MysteryResponse {
        val params = mapOf(
            "op" to "query_mystery"
        )
        return networkDataSource.request<MysteryResponse>("pot_world", params)
    }

    suspend fun signUpArena(): BasicResponse {
        val params = mapOf(
            "op" to "sign_up_arena"
        )
        return networkDataSource.request<BasicResponse>("pot_world", params)
    }

    suspend fun queryArena(): QueryArenaResponse {
        val params = mapOf(
            "op" to "query_arena"
        )
        return networkDataSource.request<QueryArenaResponse>("pot_world", params)
    }

    suspend fun fightArena(opp: Int): FightArenaResponse {
        val params = mapOf(
            "op" to "fight_arena",
            "opp" to opp,
        )
        return networkDataSource.request<FightArenaResponse>("pot_world", params)
    }

    suspend fun viewPack(): ViewPackResponse {
        val params = mapOf(
            "op" to "view_pack",
        )
        return networkDataSource.request<ViewPackResponse>("pot_world", params)
    }

    suspend fun queryExchange(): QueryExchangeResponse {
        val params = mapOf(
            "op" to "query_exchange",
        )
        return networkDataSource.request<QueryExchangeResponse>("pot_world", params)
    }

    suspend fun mysteryExchange(id: Int): BasicResponse {
        val params = mapOf(
            "op" to "mystery_exchange",
            "id" to id,
        )
        return networkDataSource.request<BasicResponse>("pot_world", params)
    }

}