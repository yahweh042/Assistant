package io.github.merlin.assistant.data.network.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LevelArray(
    @SerialName("award_array")
    val awardArray: List<AwardArray>,
    @SerialName("difficult_level")
    val difficultLevel: Int,
    @SerialName("first_pass_rold_name")
    val firstPassRoldName: String,
    @SerialName("is_special")
    val isSpecial: Int,
    @SerialName("level_id")
    val levelId: Int,
    @SerialName("npc_icon_id")
    val npcIconId: Int,
    @SerialName("score")
    val score: Int
)