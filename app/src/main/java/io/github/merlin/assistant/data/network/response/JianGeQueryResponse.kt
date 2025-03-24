package io.github.merlin.assistant.data.network.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JianGeQueryResponse(
    @SerialName("activity_end_time")
    val activityEndTime: String?,
    @SerialName("current_group")
    val currentGroup: Int?,
    @SerialName("highest_pass_floor")
    val highestPassFloor: Int?,
    @SerialName("is_get_award")
    val isGetAward: Int?,
    @SerialName("is_special")
    val isSpecial: Int?,
    @SerialName("level_array")
    val levelArray: List<LevelArray>?,
    @SerialName("max_group")
    val maxGroup: Int?,
    @SerialName("result")
    val result: Int,
    @SerialName("msg")
    val msg: String?,
) {

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
    ) {

        @Serializable
        data class AwardArray(
            @SerialName("ui_icon_desc")
            val uiIconDesc: String,
            @SerialName("ui_icon_id")
            val uiIconId: Int
        )

    }

}