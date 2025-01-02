package io.github.merlin.assistant.data.network.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AwardArray(
    @SerialName("ui_icon_desc")
    val uiIconDesc: String,
    @SerialName("ui_icon_id")
    val uiIconId: Int
)