package io.github.merlin.assistant.repo

import io.github.merlin.assistant.data.local.LocalDataSource
import io.github.merlin.assistant.data.local.model.PotSettings
import io.github.merlin.assistant.data.network.response.FightArenaResponse
import io.github.merlin.assistant.data.network.response.GetAwardResponse
import io.github.merlin.assistant.data.network.response.MysteryResponse
import io.github.merlin.assistant.data.network.response.PotResponse
import io.github.merlin.assistant.data.network.response.QueryArenaResponse
import io.github.merlin.assistant.data.network.service.PotService
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

    suspend fun queryArena(): QueryArenaResponse = potService.queryArena()

    suspend fun fightArena(opp: Int): FightArenaResponse = potService.fightArena(opp)
}