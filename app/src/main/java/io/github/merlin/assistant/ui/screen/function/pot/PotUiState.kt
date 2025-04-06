package io.github.merlin.assistant.ui.screen.function.pot

import io.github.merlin.assistant.data.local.model.PotSettings
import io.github.merlin.assistant.data.network.response.MysteryResponse
import io.github.merlin.assistant.data.network.response.PotResponse
import io.github.merlin.assistant.ui.base.ViewState

data class PotUiState(
    val logs: List<String> = listOf(),
    val potDetailViewState: ViewState = ViewState.Loading,
    val mysteryDialogState: MysteryDialogState = MysteryDialogState.Hide,
    val jobbing: Boolean = false,
    val showBottomSheet: Boolean = false,
    val potSettings: PotSettings = PotSettings(),
) {

    data class PotDetailState(
        val expLevel: Int,
        val exp: Long,
        val nextExp: Long,
        val totalExp: Long,
        val hp: Int,
        val atk: Int,
        val def: Int,

        val adventureGoods: Long,
        val adventureNum: Long,
        val bossGoods: Long,

        val gold: Long,

        val bossId: Int,
        val levelId: Int,

        val attrs: List<String>,
        val addAttr: String,
        val externalAttrs: List<String>,
        val equipments: List<PotResponse.Equipment>,
        val undisposed: List<PotResponse.Equipment>,

        val slots: List<PotResponse.Slot>,

        val upgradeGoods: Long,

        val addBlessing: Int,
    ) {

        val showUndisposedDialog
            get() = undisposed.isNotEmpty()

    }

    sealed class MysteryDialogState {
        data class Show(
            val curMysteryId: Int,
            val mysteries: Map<Int, MysteryResponse.Mystery>,
        ) : MysteryDialogState()

        data object Hide : MysteryDialogState()
        data object Loading : MysteryDialogState()
    }


}

