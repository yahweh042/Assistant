package io.github.merlin.assistant.ui.screen.function.pot

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.merlin.assistant.data.network.response.PotResponse
import io.github.merlin.assistant.ui.base.AssistantDialog
import io.github.merlin.assistant.ui.base.AssistantDropdownMenuField
import io.github.merlin.assistant.ui.base.ErrorContent
import io.github.merlin.assistant.ui.base.PagerTabIndicator
import io.github.merlin.assistant.ui.base.ViewState
import io.github.merlin.assistant.ui.screen.function.pot.settings.navigateToPotSettings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotScreen(
    navController: NavController
) {

    val viewModel: PotViewModel = hiltViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val viewState = state.potDetailViewState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "壶中天地") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigateToPotSettings() }) {
                        Image(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "",
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            when (viewState) {
                ViewState.Loading -> CircularProgressIndicator()
                is ViewState.Success<*> -> PotDetailContent(
                    potDetailState = viewState.data as PotUiState.PotDetailState,
                    jobbing = state.jobbing,
                    chooserDialogState = state.chooserDialogState,
                    logs = state.logs,
                    onEquip = { viewModel.trySendAction(PotAction.Equip(it)) },
                    onDecompose = { viewModel.trySendAction(PotAction.Decompose(it)) },
                    onBeginAdventureJob = { viewModel.trySendAction(PotAction.BeginAdventureJob) },
                    onBeginChallengeLevelJob = {
                        viewModel.trySendAction(
                            PotAction.BeginChallengeLevelJob(it)
                        )
                    },
                    onBeginChallengeBossJob = {
                        viewModel.trySendAction(PotAction.BeginChallengeBossJob(it))
                    },
                    onEndJob = { viewModel.trySendAction(PotAction.EndJob) },
                    onShowChooserDialog = { viewModel.trySendAction(PotAction.ShowChooserDialog(it)) },
                    onHideChooserDialog = { viewModel.trySendAction(PotAction.HideChooserDialog) },
                    onGetAward = remember(viewModel) {
                        { viewModel.trySendAction(PotAction.GetAward(it)) }
                    },
                )

                is ViewState.Error -> ErrorContent(
                    msg = viewState.msg,
                    retry = { viewModel.trySendAction(PotAction.RefreshPotInfo) },
                )
            }

        }
    }

}


@Composable
fun PotDetailContent(
    potDetailState: PotUiState.PotDetailState,
    jobbing: Boolean,
    logs: List<String>,
    chooserDialogState: PotUiState.ChooserDialogState,
    onEquip: (Int) -> Unit,
    onDecompose: (Int) -> Unit,
    onBeginAdventureJob: () -> Unit,
    onBeginChallengeLevelJob: (Int) -> Unit,
    onBeginChallengeBossJob: (Int) -> Unit,
    onEndJob: () -> Unit,
    onShowChooserDialog: (Int) -> Unit,
    onHideChooserDialog: () -> Unit,
    onGetAward: (String) -> Unit,
) {

    val tabLabels = listOf("日志", "装备", "属性", "外部属性")
    val pagerState = rememberPagerState(
        pageCount = { tabLabels.size },
    )

    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
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
                Text(text = "金币：${potDetailState.gold}")
            }
        }
        Row(modifier = Modifier.padding(15.dp, 5.dp)) {
            Button(onClick = { onGetAward("0") }) {
                Text(text = "领取")
            }
            Spacer(modifier = Modifier.width(5.dp))
            if (jobbing) {
                Button(onClick = onEndJob) {
                    Text(text = "停止")
                }
            } else {
                Button(onClick = onBeginAdventureJob) {
                    Text(text = "挑战")
                }
                Spacer(modifier = Modifier.width(5.dp))
                Button(onClick = { onShowChooserDialog(1) }) {
                    Text(text = "闯关")
                }
                Spacer(modifier = Modifier.width(5.dp))
                Button(onClick = { onShowChooserDialog(2) }) {
                    Text(text = "首领")
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
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
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
                0 -> LogsPage(logs)

                1 -> EquipmentPage(potDetailState.equipments)

                2 -> AttrsPage(potDetailState.attrs)

                3 -> AttrsPage(potDetailState.externalAttrs, addAttr = potDetailState.addAttr)
            }
        }
        ChooserDialog(
            currentId = 1,
            chooserDialogState = chooserDialogState,
            onChooserCancel = onHideChooserDialog,
            onChooserConfirm = { type, chooserId ->
                when (type) {
                    1 -> onBeginChallengeLevelJob(chooserId)
                    2 -> onBeginChallengeBossJob(chooserId)
                }
            },
        )
        if (potDetailState.showUndisposedDialog) {
            UndisposedDialog(
                undisposed = potDetailState.undisposed[0],
                onEquip = onEquip,
                onDecompose = onDecompose,
            )
        }
    }
}

@Composable
fun EquipmentPage(
    equipments: List<PotResponse.Equipment>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(15.dp, 5.dp)
    ) {
        items(equipments, key = { it.equipmentId }) { equipment ->
            EquipmentCard(equipment = equipment)
        }
    }
}

@Composable
fun LogsPage(
    logs: List<String>
) {

    val lazyListState = rememberLazyListState()

    LaunchedEffect(logs) {
        if (logs.isNotEmpty()) {
            lazyListState.scrollToItem(logs.size - 1)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(15.dp, 5.dp)
    ) {
        items(logs) { log ->
            Text(text = log)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AttrsPage(
    attrs: List<String>,
    addAttr: String? = null,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(15.dp, 5.dp)
    ) {
        addAttr?.let {
            stickyHeader {
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

@Composable
fun ChooserDialog(
    currentId: Int,
    chooserDialogState: PotUiState.ChooserDialogState,
    onChooserCancel: () -> Unit,
    onChooserConfirm: (type: Int, chooserId: Int) -> Unit,
) {

    var chooserId by remember { mutableIntStateOf(currentId) }

    when (chooserDialogState) {
        PotUiState.ChooserDialogState.Hide -> Unit
        is PotUiState.ChooserDialogState.Show -> AssistantDialog(
            onDismissRequest = { onChooserCancel() },
            confirmButton = {
                Button(onClick = { onChooserConfirm(chooserDialogState.type, chooserId) }) {
                    Text(text = "确定")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onChooserCancel) {
                    Text(text = "取消")
                }
            },
            title = { Text(text = "选择关卡") },
            text = {
                AssistantDropdownMenuField(
                    value = chooserId.toString(),
                    onValueChange = { chooserId = it.toInt() },
                    options = IntRange(1, 10).map { it.toString() },
                )
            }
        )
    }

}

@Composable
fun UndisposedDialog(
    undisposed: PotResponse.Equipment,
    onEquip: (equipmentId: Int) -> Unit,
    onDecompose: (equipmentId: Int) -> Unit,
) {

    AssistantDialog(
        onDismissRequest = { },
        confirmButton = {
            Button(onClick = { onEquip(undisposed.equipmentId) }) {
                Text(text = "装备")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDecompose(undisposed.equipmentId) }) {
                Text(text = "分解")
            }
        },
        title = { Text(text = "选择装备") },
        text = {
            Column {
                EquipmentCard(equipment = undisposed)
                Text(text = "当前装备", fontWeight = FontWeight.Bold)
                undisposed.equipped?.let { equipment ->
                    EquipmentCard(equipment = equipment)
                }
            }
        }
    )
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
            Text(text = "等级：${equipment.level}")
            Text(text = "类型：${equipment.type}")
            Text(text = "品质：${equipment.quality}")
            Text(text = "战力：${equipment.point}")
            Text(text = "主属性：${equipment.primaryAttrs}")
            Text(text = "次属性：${equipment.subAttrs}")
        }
    }
}