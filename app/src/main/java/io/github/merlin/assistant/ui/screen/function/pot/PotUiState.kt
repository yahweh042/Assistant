package io.github.merlin.assistant.ui.screen.function.pot

import io.github.merlin.assistant.data.network.response.PotResponse
import io.github.merlin.assistant.ui.base.ViewState

data class PotUiState(
    val logs: List<String> = listOf(),
    val potDetailViewState: ViewState = ViewState.Loading,
    val chooserDialogState: ChooserDialogState = ChooserDialogState.Hide,
    val jobbing: Boolean = false,
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
    ) {

        val showUndisposedDialog
            get() = undisposed.isNotEmpty()

    }

    sealed class ChooserDialogState {
        data class Show(val type: Int) : ChooserDialogState()
        data object Hide : ChooserDialogState()
    }

}

