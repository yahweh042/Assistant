package io.github.merlin.assistant.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class PotSettings(
    val attrFilter: Boolean = false,
    val attrs: String = "穿透,破防,最终伤害",
)