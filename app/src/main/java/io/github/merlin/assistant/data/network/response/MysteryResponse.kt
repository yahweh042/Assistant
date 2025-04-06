package io.github.merlin.assistant.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MysteryResponse(
    val result: Int,
    val msg: String?,
    val mysteries: List<Mystery> = listOf(),
) {

    @Serializable
    data class Mystery(
        @SerialName("mystery_id")
        val mysteryId: Int,
        val name: String,
        val type: Int,
        val desc: String,
        val bosses: List<Boss> = listOf(),
    ) {

        @Serializable
        data class Boss(
            @SerialName("boss_id")
            val bossId: Int,
            val name: String,
            val pass: Boolean,
            val desc: String,
            val desc2: String,
            @SerialName("drop_info")
            val dropInfo: String,
            val unlock: Int,
            @SerialName("npc_icon_id")
            val npcIconId: Int,
        )

    }

}
