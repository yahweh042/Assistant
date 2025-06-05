package io.github.merlin.assistant.ui.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsBottomSheet(
    logs: List<String> = listOf(),
    showBottomSheet: Boolean = false,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onDismissRequest: () -> Unit,
) {

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = {
            false
        },
    )

    LaunchedEffect(logs) {
        if (logs.isNotEmpty()) {
            lazyListState.scrollToItem(logs.size - 1)
        }
    }

    val animateToDismiss: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismissRequest()
            }
        }
    }

    val configuration = LocalConfiguration.current
    val sheetHeight = configuration.screenHeightDp.dp / 2

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            contentWindowInsets = { BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Horizontal) },
            dragHandle = { DragHandle(onDismissRequest = animateToDismiss) },
            shape = ShapeDefaults.Medium.copy(
                bottomEnd = CornerSize(0),
                bottomStart = CornerSize(0),
            ),
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .heightIn(min = sheetHeight, max = sheetHeight)
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 15.dp,
                    end = 15.dp,
                    bottom = paddingValues.calculateBottomPadding()
                )
            ) {
                items(logs) { log ->
                    Text(text = log)
                }
            }
        }
    }

}

@Composable
fun DragHandle(onDismissRequest: () -> Unit) {
    Row(modifier = Modifier.padding(top = 15.dp, start = 15.dp, bottom = 5.dp, end = 15.dp)) {
        Text(text = "运行日志")
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onDismissRequest, modifier = Modifier.size(18.dp)) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "",
            )
        }
    }
}