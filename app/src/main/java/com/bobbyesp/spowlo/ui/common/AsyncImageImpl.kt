package com.bobbyesp.spowlo.ui.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.disk.DiskCache
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.App.Companion.userAgentHeader
import com.bobbyesp.spowlo.R


val imageLoader = ImageLoader.Builder(context)
    .memoryCache {
        MemoryCache.Builder(context)
            .maxSizePercent(0.25)
            .build()
    }
    .diskCache {
        DiskCache.Builder()
            .directory(context.cacheDir.resolve("image_cache"))
            .maxSizePercent(0.02)
            .build()
    }
    .build()

@Composable
fun AsyncImageImpl(
    model: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    isPreview: Boolean = false
) {
    if (isPreview) Image(
        painter = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
    )
    else coil.compose.AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .addHeader("user-agent", userAgentHeader).data(model)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier,
        transform = transform,
        onState = onState,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality
    )
}