package io.github.merlin.assistant.repo

import io.github.merlin.assistant.data.local.LocalDataSource
import io.github.merlin.assistant.data.local.model.JewelSettings
import io.github.merlin.assistant.data.network.service.JewelService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class JewelRepo @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val jewelService: JewelService,
    val json: Json,
) {

    val jewelSettingsStateFlow: Flow<JewelSettings> = localDataSource.jewelSettingsFlow.map {
        it?.let { json.decodeFromString(it) } ?: JewelSettings()
    }

    val jewelSettingsState: JewelSettings
        get() = runBlocking { jewelSettingsStateFlow.first() }

    suspend fun setJewelSettings(jewelSettings: JewelSettings) {
        localDataSource.setJewelSettings(json.encodeToString(jewelSettings))
    }

    fun runTask() = flow {
        val type = jewelSettingsState.type
        val roleName = jewelSettingsState.roleName
        val facName = jewelSettingsState.factionName
        when (type) {
            1 -> {
                if (facName.isBlank()) {
                    emit("请配置帮派名")
                    return@flow
                }
                emit("当前配置: 查找帮派名 $facName")
            }
            2 -> {
                if (roleName.isBlank()) {
                    emit("请配置用户名")
                    return@flow
                }
                emit("当前配置: 查找用户名 $roleName")
            }
            else -> {
                if (facName.isBlank() || roleName.isBlank()) {
                    emit("请配置帮派+用户名")
                    return@flow
                }
                emit("当前配置: 查找帮派+用户名 $facName $roleName")
            }
        }
        for (areaId in 1 until 101) {
            emit("查看区域[$areaId]")
            val mainViewResponse = jewelService.mainView(areaId.toString())
            if (mainViewResponse.result == 0) {
                for (areaPosition in 0 until mainViewResponse.jewelList.size) {
                    val jewel = mainViewResponse.jewelList[areaPosition]
                    if ((type == 1 && jewel.facName == facName) || (type == 2 && jewel.roleName == roleName) ||
                        (type == 3 && jewel.facName == facName && jewel.roleName == roleName)
                    ) {
                        emit("找到符合要求的矿山")
                        emit("抢夺")
                        val fightResponse =
                            jewelService.fight(areaId.toString(), areaPosition.toString())
                        if (fightResponse.result == 0 && fightResponse.isWin == 1) {
                            emit("抢夺成功")
                            jewelService.jewelEnd(areaId.toString(), areaPosition.toString())
                        } else {
                            emit("抢夺失败,跳过 ${fightResponse.msg}")
                        }
                    }
                }
            } else if (mainViewResponse.result == 110) {
                emit("${mainViewResponse.msg}")
                break
            } else {
                emit("查看区域错误,跳过 ${mainViewResponse.msg}")
            }
        }
    }

}