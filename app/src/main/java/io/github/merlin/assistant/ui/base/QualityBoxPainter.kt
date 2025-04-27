package io.github.merlin.assistant.ui.base

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import io.github.merlin.assistant.R

@Composable
fun QualityBox(image: ImageBitmap) {

    val bgImage = ImageBitmap.imageResource(R.drawable.quality_b5)
    val boxImage = ImageBitmap.imageResource(R.drawable.quality_5)
    val painter = remember {
        QualityBoxPainter(
            image = image,
            bgImage = bgImage,
            boxImage = boxImage,
        )
    }

    Image(
        painter = painter,
        contentDescription = "",
    )
}

class QualityBoxPainter(
    private val image: ImageBitmap,
    private val bgImage: ImageBitmap,
    private val boxImage: ImageBitmap,
    private val dstSize: IntSize = IntSize(image.width, image.height),
) : Painter() {

    override val intrinsicSize: Size
        get() = IntSize(dstSize.width + 12, dstSize.height + 12).toSize()

    override fun DrawScope.onDraw() {
        drawImage(
            image = bgImage,
            dstOffset = IntOffset(2, 2),
            dstSize = IntSize(dstSize.width + 8, dstSize.height + 8),
        )
        drawImage(image = boxImage, dstSize = IntSize(dstSize.width + 12, dstSize.height + 12))
        drawImage(image = image, dstOffset = IntOffset(6, 6), dstSize = dstSize)
    }

}