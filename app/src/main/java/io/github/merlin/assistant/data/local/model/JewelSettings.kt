package io.github.merlin.assistant.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class JewelSettings(
    val roleName: String = "",
    val factionName: String = "",
    val buyVit: Boolean = false,
    val chooseSecretReel: String = "",
    val type: Int = 1,
)
