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
)