package io.github.merlin.assistant.ui.screen.function.pot

sealed class PotAction {
    data class BeginChallengeLevelJob(val levelId: Int) : PotAction()
    data class BeginChallengeBossJob(val bossId: Int) : PotAction()
    data object BeginAdventureJob : PotAction()
    data object EndJob : PotAction()
    data object RefreshPotInfo : PotAction()
    data object ShowMysteryDialog : PotAction()
    data object HideMysteryDialog : PotAction()
    data class SwitchMystery(val mysteryId: Int): PotAction()
    data object HideBottomSheet : PotAction()

    data class Equip(val equipmentId: Int) : PotAction()
    data class Decompose(val equipmentId: Int) : PotAction()

    data class GetAward(val type: String) : PotAction()

    data class UpgradeSlot(val type: String) : PotAction()

}