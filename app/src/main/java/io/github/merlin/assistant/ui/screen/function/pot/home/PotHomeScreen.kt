package io.github.merlin.assistant.ui.screen.function.pot.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import io.github.merlin.assistant.data.network.response.PotResponse
import io.github.merlin.assistant.ui.base.AssistantDialog
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.LaunchedEvent
import io.github.merlin.assistant.ui.base.LoadingDialog
import io.github.merlin.assistant.ui.base.LoadingDialogState
import io.github.merlin.assistant.ui.base.LogsBottomSheet
import io.github.merlin.assistant.ui.base.PagerTabIndicator
import io.github.merlin.assistant.ui.base.SkillIcon
import io.github.merlin.assistant.ui.base.ViewState
import io.github.merlin.assistant.ui.base.WeaponIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotHomePage(
    paddingValues: PaddingValues,
) {

    val viewModel: PotHomeViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState = state.viewState
    val context = LocalContext.current

    LaunchedEvent(viewModel = viewModel) { event ->
        when (event) {
            is PotHomeEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT)
                .show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (viewState) {
            ViewState.Loading -> CircularProgressIndicator()
            is ViewState.Success<PotHomeUiState.PotDetailState> -> PotDetailContent(
                paddingValues = paddingValues,
                potDetailState = viewState.data,
                jobbing = state.jobbing,
                onBeginAdventureJob = { viewModel.trySendAction(PotHomeAction.BeginAdventureJob) },
                onBeginChallengeLevelJob = {
                    viewModel.trySendAction(PotHomeAction.BeginChallengeLevelJob(it))
                },
                onEndJob = { viewModel.trySendAction(PotHomeAction.EndJob) },
                onShowMysteryDialog = { viewModel.trySendAction(PotHomeAction.ShowMysteryDialog) },
                onGetAward = remember(viewModel) {
                    { viewModel.trySendAction(PotHomeAction.GetAward(it)) }
                },
                onUpgradeSlot = remember(viewModel) {
                    { viewModel.trySendAction(PotHomeAction.UpgradeSlot(it)) }
                },
            )

            is ViewState.Error -> ErrorContent(
                msg = viewState.msg,
                retry = { viewModel.trySendAction(PotHomeAction.RefreshPotInfo) },
            )
        }

    }
    UndisposedDialog(
        state = state.undisposedDialogState,
        onEquip = { viewModel.trySendAction(PotHomeAction.Equip(it)) },
        onDecompose = { viewModel.trySendAction(PotHomeAction.Decompose(it)) },
    )
    MysteryDialog(
        state = state.mysteryDialogState,
        onHideMysteryDialog = { viewModel.trySendAction(PotHomeAction.HideMysteryDialog) },
        onSwitchMystery = { viewModel.trySendAction(PotHomeAction.SwitchMystery(it)) },
        onBeginChallengeBossJob = { viewModel.trySendAction(PotHomeAction.BeginChallengeBossJob(it)) },
    )
    LogsBottomSheet(
        logs = state.logs,
        showBottomSheet = state.showBottomSheet,
        paddingValues = paddingValues,
        onDismissRequest = {
            viewModel.trySendAction(PotHomeAction.HideBottomSheet)
        }
    )
    LoadingDialog(
        loadingDialogState = state.loadingDialogState,
    )
}


@Composable
fun PotDetailContent(
    paddingValues: PaddingValues,
    potDetailState: PotHomeUiState.PotDetailState,
    jobbing: Boolean,
    onBeginAdventureJob: () -> Unit,
    onBeginChallengeLevelJob: (Int) -> Unit,
    onEndJob: () -> Unit,
    onShowMysteryDialog: () -> Unit,
    onGetAward: (String) -> Unit,
    onUpgradeSlot: (String) -> Unit,
) {

    val tabLabels = listOf("装备", "强化", "属性", "外部属性")
    val pagerState = rememberPagerState(
        pageCount = { tabLabels.size },
    )

    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp, 0.dp)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    text = "等级：${potDetailState.expLevel} (${potDetailState.exp}/${potDetailState.nextExp})",
                    fontWeight = FontWeight.Bold,
                )
                Row {
                    Text(
                        text = "挑战次数：${potDetailState.adventureGoods}",
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "闯关次数：${potDetailState.bossGoods}",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row {
                    Text(
                        text = "当前关卡：${potDetailState.levelId}",
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "当前首领：${potDetailState.bossId}", modifier = Modifier.weight(1f))
                }
                Row {
                    Text(
                        text = "装备碎片：${potDetailState.upgradeGoods}",
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "金币：${potDetailState.gold}", modifier = Modifier.weight(1f))
                }
            }
        }
        Row(
            modifier = Modifier.padding(15.dp, 5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Button(onClick = { onGetAward("0") }) {
                Text(text = "领取")
            }
            if (jobbing) {
                Button(onClick = onEndJob) {
                    Text(text = "停止")
                }
            } else {
                Button(onClick = onBeginAdventureJob) {
                    Text(text = "挑战")
                }
                Button(onClick = { onBeginChallengeLevelJob(potDetailState.levelId) }) {
                    Text(text = "闯关")
                }
                Button(onClick = { onShowMysteryDialog() }) {
                    Text(text = "秘境")
                }
            }
        }
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 15.dp,
            divider = {},
            indicator = { tabPositions ->
                PagerTabIndicator(
                    pagerState = pagerState,
                    tabPositions = tabPositions,
                )
            }
        ) {
            tabLabels.fastForEachIndexed { index, tabLabel ->
                val selected = index == pagerState.currentPage
                Tab(
                    modifier = Modifier.height(48.dp),
                    selected = selected,
                    onClick = { scope.launch { pagerState.scrollToPage(index) } },
                ) {
                    Text(
                        text = tabLabel,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 -> EquipmentPage(
                    paddingValues = paddingValues,
                    equipments = potDetailState.equipments,
                )

                1 -> SlotPage(
                    paddingValues = paddingValues,
                    slots = potDetailState.slots,
                    onUpgradeSlot = onUpgradeSlot,
                )

                2 -> AttrsPage(paddingValues = paddingValues, attrs = potDetailState.attrs)

                3 -> AttrsPage(
                    paddingValues = paddingValues,
                    attrs = potDetailState.externalAttrs,
                    addAttr = potDetailState.addAttr,
                )
            }
        }
    }
}

@Composable
fun EquipmentPage(
    paddingValues: PaddingValues,
    equipments: List<PotResponse.Equipment>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues.innerPagePaddingValues(),
    ) {
        items(equipments, key = { it.equipmentId }) { equipment ->
            EquipmentCard(equipment = equipment)
        }
    }
}

@Composable
fun AttrsPage(
    paddingValues: PaddingValues,
    attrs: List<String>,
    addAttr: String? = null,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues.innerPagePaddingValues()
    ) {
        addAttr?.let {
            item {
                Text(
                    text = "属性加成：${it}",
                    fontWeight = FontWeight.Bold
                )
            }
        }
        items(attrs) { attr ->
            Text(text = attr)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MysteryDialog(
    state: PotHomeUiState.MysteryDialogState,
    onHideMysteryDialog: () -> Unit,
    onSwitchMystery: (mysteryId: Int) -> Unit,
    onBeginChallengeBossJob: (bossId: Int) -> Unit,
) {

    val configuration = LocalConfiguration.current
    val dialogHeight = configuration.screenHeightDp.dp / 2
    val dialogWidth = configuration.screenWidthDp.dp - 80.dp

    val bosses =
        if (state is PotHomeUiState.MysteryDialogState.Show) state.mysteries[state.curMysteryId]?.bosses
            ?: listOf() else listOf()
    val listState = rememberLazyListState()

    LaunchedEffect(bosses) {
        if (bosses.isNotEmpty()) {
            var index = bosses.indexOfFirst { !it.pass }
            if (index > 1) {
                index -= 1
            }
            listState.animateScrollToItem(index)
        }
    }

    when (state) {
        PotHomeUiState.MysteryDialogState.Hide -> Unit
        PotHomeUiState.MysteryDialogState.Loading -> LoadingDialog(
            loadingDialogState = LoadingDialogState.Loading()
        )

        is PotHomeUiState.MysteryDialogState.Show -> BasicAlertDialog(
            onDismissRequest = { onHideMysteryDialog() },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
            ),
            modifier = Modifier.widthIn(max = dialogWidth),
        ) {
            Surface(
                shape = ShapeDefaults.Medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Column(
                    modifier = Modifier.padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row {
                        state.mysteries.values.forEach { mystery ->
                            TextButton(
                                onClick = { onSwitchMystery(mystery.mysteryId) },
                                colors = if (state.curMysteryId == mystery.mysteryId) ButtonDefaults.elevatedButtonColors() else ButtonDefaults.textButtonColors(),
                            ) {
                                Text(text = mystery.name)
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onHideMysteryDialog) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = "")
                        }
                    }
                    Surface(shape = ShapeDefaults.Medium) {
                        LazyColumn(
                            modifier = Modifier.height(dialogHeight),
                            state = listState,
                        ) {
                            items(bosses) { boss ->
                                ListItem(
                                    headlineContent = { Text(text = "[${boss.bossId}] ${boss.name}") },
                                    supportingContent = { Text(text = boss.desc) },
                                    trailingContent = {
                                        Text(
                                            text = when {
                                                boss.pass -> "已通关"
                                                else -> "${boss.unlock}级解锁"
                                            }
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        onBeginChallengeBossJob(boss.bossId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun UndisposedDialog(
    state: PotHomeUiState.UndisposedDialogState,
    onEquip: (equipmentId: Int) -> Unit,
    onDecompose: (equipmentId: Int) -> Unit,
) {

    when (state) {
        is PotHomeUiState.UndisposedDialogState.Hide -> Unit
        is PotHomeUiState.UndisposedDialogState.Show -> AssistantDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = { onEquip(state.undisposed.equipmentId) }) {
                    Text(text = "装备")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDecompose(state.undisposed.equipmentId) }) {
                    Text(text = "分解")
                }
            },
            title = { Text(text = "选择装备") },
            text = {
                Column {
                    EquipmentCard(equipment = state.undisposed)
                    Text(text = "当前装备", fontWeight = FontWeight.Bold)
                    state.undisposed.equipped?.let { equipment ->
                        EquipmentCard(equipment = equipment)
                    }
                }
            }
        )
    }

}

@Composable
fun EquipmentCard(equipment: PotResponse.Equipment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 5.dp),
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(text = equipment.name, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.padding(vertical = 5.dp)) {
                HorizontalDivider(modifier = Modifier.height(Dp.Hairline))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.Center) {
                    when {
                        equipment.type >= 1 && equipment.type <= 3 -> WeaponIcon(
                            id = equipment.refId,
                            level = equipment.refLevel,
                        )

                        equipment.type >= 4 && equipment.type <= 5 -> SkillIcon(
                            id = equipment.refId,
                            level = equipment.refLevel
                        )

                        else -> SubcomposeAsyncImage(
                            model = "https://res.ledou.qq.com/ledoures/potworld/lv0/pic/${equipment.refId}.png",
                            loading = { CircularProgressIndicator(modifier = Modifier.padding(10.dp)) },
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                        )
                    }
                }
                Column(modifier = Modifier.padding(start = 5.dp)) {
                    Text(text = "等级：${equipment.level}")
                    Text(text = "类型：${equipment.type}")
                    Text(text = "品质：${equipment.quality}")
                }
            }
            Row(modifier = Modifier.padding(vertical = 5.dp)) {
                HorizontalDivider(modifier = Modifier.height(Dp.Hairline))
            }
            Text(text = "战力：${equipment.point}")
            Text(text = "主属性：${equipment.primaryAttrs}")
            Text(text = "次属性：${equipment.subAttrs}")
        }
    }
}

@Composable
fun SlotPage(
    paddingValues: PaddingValues,
    slots: List<PotResponse.Slot>,
    onUpgradeSlot: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues.innerPagePaddingValues(),
    ) {
        items(slots, key = { it.type }) { slot ->
            SlotCard(slot = slot, onUpgradeSlot = onUpgradeSlot)
        }
    }
}

@Composable
fun SlotCard(
    slot: PotResponse.Slot,
    onUpgradeSlot: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 5.dp),
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(text = "类型：${slot.type}")
            Text(text = "等级：${slot.level}/${slot.maxLevel}")
            Text(text = "祝福：${slot.blessing}/${slot.maxBlessing}")
            Text(text = "描述：${slot.desc}")
            Text(text = "消耗：${slot.upgradeCostDesc}")
            Text(text = "成功率：${slot.rateDesc}")
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = { onUpgradeSlot(slot.type.toString()) }) {
                    Text(text = "强化")
                }
            }
        }
    }
}

fun PaddingValues.innerPagePaddingValues(): PaddingValues {
    return PaddingValues(
        start = 15.dp,
        end = 15.dp,
        top = 5.dp,
        bottom = this.calculateBottomPadding(),
    )
}