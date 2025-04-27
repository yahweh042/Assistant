package io.github.merlin.assistant.ui.base

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@Composable
fun GoodsIcon(
    iconId: Long,
    id: Int? = null,
    size: Int = 48,
) {
    val model = when (id) {
        400001 -> "https://res.ledou.qq.com/ledoures/goods/kCash.png"
        400002 -> "https://res.ledou.qq.com/ledoures/goods/kExp.png"
        else -> "https://res.ledou.qq.com/ledoures/goods/${iconId}.png"
    }
    SubcomposeAsyncImage(
        model = model,
        loading = { CircularProgressIndicator(modifier = Modifier.padding(6.dp)) },
        contentDescription = null,
        modifier = Modifier.size(size.dp),
    )
}

@Composable
fun WeaponIcon(
    id: Long,
    level: Int,
) {
    SubcomposeAsyncImage(
        model = "https://res.ledou.qq.com/ledoures/weapon/lv$level/pic/$id.png",
        loading = { CircularProgressIndicator(modifier = Modifier.padding(10.dp)) },
        contentDescription = null,
        modifier = Modifier.size(60.dp),
    )
}

@Composable
fun SkillIcon(
    id: Long,
    level: Int,
) {
    SubcomposeAsyncImage(
        model = "https://res.ledou.qq.com/ledoures/skill/lv$level/pic/$id.png",
        loading = { CircularProgressIndicator(modifier = Modifier.padding(10.dp)) },
        contentDescription = null,
        modifier = Modifier.size(60.dp),
    )
}

@Composable
fun ShopIcon(id: String) {
    val model = when (id) {
        "1" -> "https://res.ledou.qq.com/ledoures/goods/kCash.png"
        else -> "https://res.ledou.qq.com/ledoures/shop/ico$id.png"
    }
    SubcomposeAsyncImage(
        model = model,
        loading = { CircularProgressIndicator(modifier = Modifier.padding(2.dp)) },
        contentDescription = null,
        modifier = Modifier.size(16.dp),
    )
}

@Composable
fun HeadImg(headImgUrl: String) {

    val model = headImgUrl.replace("http:", "https:")

    SubcomposeAsyncImage(
        model = model,
        contentDescription = null,
        modifier = Modifier.size(40.dp),
        error = {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
            )
        },
        loading = {
            CircularProgressIndicator()
        }
    )
}