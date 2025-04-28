package io.github.merlin.assistant.repo

import io.github.merlin.assistant.data.local.LocalDataSource
import io.github.merlin.assistant.data.local.model.PotSettings
import io.github.merlin.assistant.data.network.response.BasicResponse
import io.github.merlin.assistant.data.network.response.FightArenaResponse
import io.github.merlin.assistant.data.network.response.GetAwardResponse
import io.github.merlin.assistant.data.network.response.GoodsInfo
import io.github.merlin.assistant.data.network.response.MysteryResponse
import io.github.merlin.assistant.data.network.response.PotResponse
import io.github.merlin.assistant.data.network.response.QueryArenaResponse
import io.github.merlin.assistant.data.network.response.ViewPackResponse
import io.github.merlin.assistant.data.network.service.PotService
import io.github.merlin.assistant.repo.model.QueryExchangeResult
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PotRepo @Inject constructor(
    private val potService: PotService,
    private val localDataSource: LocalDataSource,
    private val json: Json,
) {

    val potSettingsStateFlow = localDataSource.potSettingsFlow.map {
        it?.let { json.decodeFromString(it) } ?: PotSettings()
    }

    suspend fun setPotSettings(potSettings: PotSettings) {
        localDataSource.setPotSettings(json.encodeToString(potSettings))
    }

    suspend fun index(): PotResponse {
        return potService.index()
    }

    suspend fun getAward(type: String): GetAwardResponse {
        return potService.getAward(type)
    }

    suspend fun challengeLevel(levelId: String): PotResponse {
        return potService.challengeLevel(levelId)
    }

    suspend fun challengeBoss(bossId: String): PotResponse {
        return potService.challengeBoss(bossId)
    }

    suspend fun adventure(): PotResponse {
        return potService.adventure()
    }

    suspend fun decompose(equipmentId: Int): PotResponse {
        return potService.decompose(equipmentId)
    }

    suspend fun equip(equipmentId: String): PotResponse {
        return potService.equip(equipmentId)
    }

    suspend fun upgradeSlot(type: String): PotResponse {
        return potService.upgradeSlot(type)
    }

    suspend fun queryMystery(): MysteryResponse = potService.queryMystery()

    suspend fun signUpArena() = potService.signUpArena()

    suspend fun queryArena(): QueryArenaResponse = potService.queryArena()

    suspend fun fightArena(opp: Int): FightArenaResponse = potService.fightArena(opp)

    suspend fun viewPack(): ViewPackResponse = potService.viewPack()

    suspend fun queryExchange(): QueryExchangeResult {
        val goodsInfoMap = mutableMapOf<Int, GoodsInfo>()
        val viewPackResponse = viewPack()
        if (viewPackResponse.result == 0) {
            viewPackResponse.goodsInfo?.forEach { goodsInfo ->
                goodsInfoMap[goodsInfo.id] = goodsInfo
            }
        }
        val queryExchangeResponse = potService.queryExchange()
        return if (queryExchangeResponse.result == 0) {
            val exchange = queryExchangeResponse.exchanges.map {
                it.copy(goodsInfo = goodsInfoMap[it.goodsId])
            }.sortedBy { it.id }
            return QueryExchangeResult.Success(exchange)
        } else {
            return QueryExchangeResult.Error("${queryExchangeResponse.msg}")
        }
    }

    suspend fun mysteryExchange(id: Int): BasicResponse = potService.mysteryExchange(id)

}