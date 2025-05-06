package io.github.merlin.assistant.ui.screen.function.storage

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import io.github.merlin.assistant.ui.screen.function.storage.page.StoragePage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(navController: NavController) {

    val pagerState = rememberPagerState { StorageType.entries.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "",
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StorageType.entries.forEachIndexed { index, entry ->
                            TextButton(
                                onClick = { scope.launch { pagerState.scrollToPage(index) } },
                                colors = if (index == pagerState.currentPage) ButtonDefaults.elevatedButtonColors() else ButtonDefaults.textButtonColors(),
                            ) {
                                Text(text = entry.label)
                            }
                        }
                    }
                },
            )
        }
    ) { paddingValues ->
        HorizontalPager(state = pagerState) { pageIndex ->
            StoragePage(
                type = StorageType.entries[pageIndex].type,
                contentPadding = paddingValues,
            )
        }
    }

}

enum class StorageType(
    val type: Int,
    val label: String,
) {

    MEDICINE(1, "药水"),
    UPGRADE(2, "强化"),
    OTHER(3, "其它");

}