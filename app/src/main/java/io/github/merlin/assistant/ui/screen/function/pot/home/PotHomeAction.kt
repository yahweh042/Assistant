package io.github.merlin.assistant.ui.screen.function.pot.home

sealed class PotHomeAction {
    data class BeginChallengeLevelJob(val levelId: Int) : PotHomeAction()
    data class BeginChallengeBossJob(val bossId: Int) : PotHomeAction()
    data object BeginAdventureJob : PotHomeAction()
    data object EndJob : PotHomeAction()
    data object RefreshPotInfo : PotHomeAction()
    data object ShowMysteryDialog : PotHomeAction()
    data object HideMysteryDialog : PotHomeAction()
    data class SwitchMystery(val mysteryId: Int): PotHomeAction()
    data object HideBottomSheet : PotHomeAction()

    data class Equip(val equipmentId: Int) : PotHomeAction()
    data class Decompose(val equipmentId: Int) : PotHomeAction()

    data class GetAward(val type: String) : PotHomeAction()

    data class UpgradeSlot(val type: String) : PotHomeAction()

}