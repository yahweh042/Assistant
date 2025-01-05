package io.github.merlin.assistant.ui.base

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@Composable
fun GoodsIcon(
    iconId: Long
) {
    SubcomposeAsyncImage(
        model = "https://res.ledou.qq.com/ledoures/goods/${iconId}.png",
        loading = { CircularProgressIndicator() },
        contentDescription = null,
        modifier = Modifier.size(48.dp),
    )
}

@Composable
fun WeaponIcon(
    id: Long,
    level: Int,
) {
    SubcomposeAsyncImage(
        model = "https://res.ledou.qq.com/ledoures/weapon/lv$level/pic/$id.png",
        loading = { CircularProgressIndicator() },
        contentDescription = null,
        modifier = Modifier.size(48.dp),
    )
}

@Composable
fun SkillIcon(
    id: Long,
    level: Int,
) {
    SubcomposeAsyncImage(
        model = "https://res.ledou.qq.com/ledoures/skill/lv$level/pic/$id.png",
        loading = { CircularProgressIndicator() },
        contentDescription = null,
        modifier = Modifier.size(48.dp),
    )
}