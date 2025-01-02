package io.github.merlin.assistant.data.network.response

import io.github.merlin.assistant.ui.screen.function.pot.PotUiState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PotResponse(
    val result: Int,
    val msg: String = "",

    @SerialName("exp_level")
    val expLevel: Int?,
    val exp: Long?,
    @SerialName("next_exp")
    val nextExp: Long?,
    @SerialName("total_exp")
    val totalExp: Long?,
    @SerialName("HP")
    val hp: Int?,
    @SerialName("ATK")
    val atk: Int?,
    @SerialName("DEF")
    val def: Int?,

    @SerialName("adventure_goods")
    val adventureGoods: Long?,
    @SerialName("adventure_num")
    val adventureNum: Long?,
    @SerialName("boss_goods")
    val bossGoods: Long?,

    val gold: Long?,

    @SerialName("boss_id")
    val bossId: Int?,
    @SerialName("level_id")
    val levelId: Int?,

    @SerialName("equipments")
    val equipments: List<Equipment>?,
    @SerialName("undisposed")
    val undisposed: List<Equipment>?,

    @SerialName("passed")
    val passed: Int?,

    @SerialName("attrs")
    val attrs: String?,

    @SerialName("add_attr")
    val addAttr: String?,

    @SerialName("external_attrs")
    val externalAttrs: String?,
) {

    fun toPotInfo(): PotUiState.PotDetailState {
        return PotUiState.PotDetailState(
            expLevel = expLevel ?: 0,
            exp = exp ?: 0,
            nextExp = nextExp ?: 0,
            totalExp = totalExp ?: 0,
            hp = hp ?: 0,
            atk = atk ?: 0,
            def = def ?: 0,
            adventureGoods = adventureGoods ?: 0,
            adventureNum = adventureNum ?: 0,
            bossGoods = bossGoods ?: 0,
            gold = gold ?: 0,
            bossId = bossId ?: 0,
            levelId = levelId ?: 0,
            attrs = (attrs ?: "").split(" "),
            equipments = equipments ?: listOf(),
            undisposed = undisposed ?: listOf(),
            addAttr = addAttr ?: "0%",
            externalAttrs = (externalAttrs ?: "").split(" ")
        )
    }

    @Serializable
    data class Equipment(
        @SerialName("equipment_id")
        val equipmentId: Int,
        @SerialName("name")
        val name: String,
        @SerialName("desc")
        val desc: String,
        @SerialName("level")
        val level: Int,
        @SerialName("quality")
        val quality: Int,
        @SerialName("type")
        val type: Int,
        @SerialName("primary_attrs")
        val primaryAttrs: String,
        @SerialName("sub_attrs")
        val subAttrs: String,
        @SerialName("point")
        val point: Int,

        @SerialName("equipped")
        val equipped: Equipment?,
    )

}
