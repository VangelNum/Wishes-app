package com.vangelnum.wisher.features.home.getwish.presentation

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Size
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.core.presentation.SmallLoadingIndicator
import com.vangelnum.wisher.features.auth.core.model.User
import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import com.vangelnum.wisher.features.widget.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Path as ComposePath

@Composable
fun GetWishScreenToBottomSheet(
    state: UiState<Wish>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        when (state) {
            is UiState.Error -> {
                ErrorScreen(errorMessage = state.message)
            }

            is UiState.Idle -> {}

            is UiState.Loading -> {
                LoadingScreen(loadingText = stringResource(R.string.loading_wish))
            }

            is UiState.Success -> {
                WishContent(state.data)
            }
        }
    }
}

@Composable
fun WishContent(wish: Wish) {

    Column(modifier = Modifier.padding(16.dp)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 16.dp)
            ) {
                if (wish.user.avatarUrl == null) {
                    Image(
                        painter = painterResource(R.drawable.defaultprofilephoto),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = wish.user.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.defaultprofilephoto),
                        error = painterResource(R.drawable.defaultprofilephoto)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = wish.user.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedText(
                text = wish.text,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                animationDelayPerChar = 50
            )

            Card(
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                if (wish.isBlurred) {
                    InteractiveBlurredImage(
                        imageUrl = wish.image,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    SubcomposeAsyncImage(
                        model = wish.image,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        loading = { SmallLoadingIndicator() },
                        error = {
                            Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                                ErrorScreen(stringResource(R.string.error_loading_image))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    animationDelayPerChar: Long
) {
    val displayedText = remember { mutableStateOf("") }

    LaunchedEffect(key1 = text) {
        displayedText.value = ""
        text.forEachIndexed { index, char ->
            displayedText.value = text.substring(0, index + 1)
            delay(animationDelayPerChar)
        }
    }

    Text(
        text = displayedText.value,
        style = style,
        modifier = modifier
    )
}

@Composable
fun InteractiveBlurredImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    blurRadius: Int = 25,
    revealStrokeWidth: Float = 80f
) {
    val context = LocalContext.current
    val imageLoader = context.imageLoader

    val revealedPath = remember { mutableStateOf(ComposePath()) }
    val currentPosition = remember { mutableStateOf<Offset?>(null) }

    val imageState = produceState<Pair<ImageBitmap?, ImageBitmap?>>(
        initialValue = Pair(null, null),
        key1 = imageUrl,
        key2 = blurRadius
    ) {
        value = Pair(null, null)
        var clearBitmap: ImageBitmap? = null
        var blurredBitmap: ImageBitmap? = null

        try {
            val clearRequest = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .size(Size.ORIGINAL)
                .build()
            val clearResult = imageLoader.execute(clearRequest)
            val clearDrawable = clearResult.drawable
            val clearAndroidBitmap = (clearDrawable as? BitmapDrawable)?.bitmap

            if (clearAndroidBitmap != null) {
                val mutableClearBitmap = if (clearAndroidBitmap.isMutable) {
                    clearAndroidBitmap
                } else {
                    clearAndroidBitmap.copy(Bitmap.Config.ARGB_8888, true)
                }
                clearBitmap = mutableClearBitmap.asImageBitmap()

                val blurredAndroidBitmap = withContext(Dispatchers.IO) {
                    BlurTransformation(radius = blurRadius).transform(mutableClearBitmap, Size.ORIGINAL)
                }

                if (blurredAndroidBitmap.width == clearBitmap.width && blurredAndroidBitmap.height == clearBitmap.height) {
                    blurredBitmap = blurredAndroidBitmap.asImageBitmap()
                } else {
                    Log.e("BlurImage", "Blurred bitmap dimensions mismatch!")
                    val scaledBlurred = blurredAndroidBitmap.scale(clearBitmap.width, clearBitmap.height)
                    blurredBitmap = scaledBlurred.asImageBitmap()
                }


            } else {
                Log.d("BlurImage", "Error: Could not load bitmap from drawable")
            }
            value = Pair(clearBitmap, blurredBitmap)
        } catch (e: Exception) {
            Log.d("BlurImage", "Error loading/blurring image: $e")
            value = Pair(null, null)
        }
    }


    val (clearBitmap, blurredBitmap) = imageState.value

    Box(modifier = modifier.clip(RoundedCornerShape(32.dp))) {
        if (clearBitmap != null && blurredBitmap != null) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { startOffset ->
                                currentPosition.value = startOffset
                                revealedPath.value = ComposePath().apply {
                                    addPath(revealedPath.value)
                                    moveTo(startOffset.x, startOffset.y)
                                }
                            },
                            onDrag = { change, _ ->
                                val newPosition = change.position
                                currentPosition.value = newPosition
                                revealedPath.value = ComposePath().apply {
                                    addPath(revealedPath.value)
                                    lineTo(newPosition.x, newPosition.y)
                                }
                                change.consume()
                            },
                            onDragEnd = { currentPosition.value = null },
                            onDragCancel = { currentPosition.value = null }
                        )
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val imageWidth = clearBitmap.width.toFloat()
                val imageHeight = clearBitmap.height.toFloat()

                val dstSize = IntSize(canvasWidth.roundToInt(), canvasHeight.roundToInt())
                val dstOffset = IntOffset.Zero

                var srcOffset = IntOffset.Zero
                var srcSize = IntSize(imageWidth.roundToInt(), imageHeight.roundToInt())

                if (imageWidth > 0 && imageHeight > 0 && canvasWidth > 0 && canvasHeight > 0) {
                    val canvasRatio = canvasWidth / canvasHeight
                    val imageRatio = imageWidth / imageHeight

                    val scale: Float
                    if (imageRatio > canvasRatio) {
                        scale = canvasHeight / imageHeight
                        val srcWidth = (canvasWidth / scale).roundToInt()
                        val srcOffsetX = ((imageWidth - srcWidth) / 2f).roundToInt()
                        srcOffset = IntOffset(srcOffsetX.coerceAtLeast(0), 0)
                        srcSize = IntSize(srcWidth.coerceAtMost(imageWidth.roundToInt()), imageHeight.roundToInt()) // Убедимся, что размер не больше исходного
                    } else {
                        scale = canvasWidth / imageWidth
                        val srcHeight = (canvasHeight / scale).roundToInt()
                        val srcOffsetY = ((imageHeight - srcHeight) / 2f).roundToInt()
                        srcOffset = IntOffset(0, srcOffsetY.coerceAtLeast(0))
                        srcSize = IntSize(imageWidth.roundToInt(), srcHeight.coerceAtMost(imageHeight.roundToInt())) // Убедимся, что размер не больше исходного
                    }

                }
                if (srcSize.width > 0 && srcSize.height > 0) {
                    // 1. Рисуем оригинальное изображение (обрезанное) как нижний слой
                    drawImage(
                        image = clearBitmap,
                        srcOffset = srcOffset,
                        srcSize = srcSize,
                        dstOffset = dstOffset,
                        dstSize = dstSize // Растягиваем выбранный кусок на весь холст
                    )

                    // 2. Рисуем размытое изображение поверх с тем же кадрированием
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply { alpha = 0.99f }
                        canvas.saveLayer(size.toRect(), paint)

                        drawImage(
                            image = blurredBitmap,
                            srcOffset = srcOffset,
                            srcSize = srcSize,
                            dstOffset = dstOffset,
                            dstSize = dstSize // Растягиваем тот же кусок на весь холст
                        )

                        // 3. "Стираем" размытый слой
                        drawPath(
                            path = revealedPath.value,
                            color = Color.Black,
                            style = Stroke(
                                width = revealStrokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            ),
                            blendMode = BlendMode.DstOut
                        )

                        canvas.restore()
                    }
                } else {
                    Log.w("BlurImage", "Calculated srcSize is invalid: $srcSize. Skipping draw.")
                    drawRect(Color.Gray)
                }
            }
        } else if (imageState.value == Pair(null, null) && imageUrl.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                SmallLoadingIndicator()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.error_loading_image))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GetWishScreenToBottomSheetPreview() {
    GetWishScreenToBottomSheet(
        state = UiState.Success(
            Wish(
                id = 1,
                user = User(
                    id = 1,
                    name = "John Doe",
                    avatarUrl = null,
                    email = "vangel@mail.ru",
                    role = "",
                    coins = 100,
                    password = "",
                    isEmailVerified = true,
                    verificationCode = null
                ),
                text = "Happy birthday! Wishing you all the best on this special day.",
                image = "https://picsum.photos/seed/preview1/200",
                openDate = "2023-01-01T00:00:00Z",
                wishDate = "2023-01-01T00:00:00Z",
                maxViewers = 10,
                cost = 50,
                isBlurred = true
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun GetWishScreenToBottomSheetNotBlurredPreview() {
    GetWishScreenToBottomSheet(
        state = UiState.Success(
            Wish(
                id = 2,
                user = User(
                    id = 2,
                    name = "Jane Smith",
                    avatarUrl = "https://picsum.photos/seed/avatar2/50",
                    email = "jane@mail.ru",
                    role = "",
                    coins = 200,
                    password = "",
                    isEmailVerified = true,
                    verificationCode = null
                ),
                text = "Congratulations!",
                image = "https://picsum.photos/seed/preview2/200",
                openDate = "2024-01-01T00:00:00Z",
                wishDate = "2024-01-01T00:00:00Z",
                maxViewers = 5,
                cost = 20,
                isBlurred = false
            )
        )
    )
}