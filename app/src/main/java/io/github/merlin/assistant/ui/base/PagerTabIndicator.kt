package io.github.merlin.assistant.ui.base

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlin.math.abs

@Composable
fun PagerTabIndicator(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    @FloatRange(from = 0.0, to = 1.0) percent: Float = 0.6f,
    height: Dp = 3.dp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val currentPage = minOf(tabPositions.lastIndex, pagerState.currentPage)
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffsetFraction

        val indicatorWidth = currentTab.width.toPx() * percent

        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).toPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).toPx()
        } else {
            currentTab.left.toPx()
        }

        val canvasHeight = size.height
        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = indicatorOffset + (currentTab.width.toPx() * (1 - percent) / 2),
                y = canvasHeight - height.toPx()
            ),
            size = Size(indicatorWidth + indicatorWidth * abs(fraction), height.toPx()),
            cornerRadius = CornerRadius(50f)
        )
    }
}