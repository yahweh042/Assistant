package io.github.merlin.assistant.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class PotSettings(
    val equips: Map<Int, List<Equip>> = mapOf(),
    val needSubAttrs: List<String> = listOf(),
) {

    @Serializable
    data class Equip(
        val id: Long,
        val name: String,
    )
}