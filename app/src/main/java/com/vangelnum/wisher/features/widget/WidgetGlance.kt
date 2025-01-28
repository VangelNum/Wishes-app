package com.vangelnum.wisher.features.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import coil.ImageLoader
import coil.request.ImageRequest
import com.vangelnum.wisher.App.Companion.dataStore
import com.vangelnum.wisher.MainActivity
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import com.vangelnum.wisher.features.home.getwish.domain.repository.GetWishRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull

class WidgetGlance : GlanceAppWidget() {

    val widget_wish_key = stringPreferencesKey("widget_wish_key")

    override var stateDefinition = PreferencesGlanceStateDefinition

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetGlanceEntryPoint {
        fun getWishRepository(): GetWishRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.dataStore.data.firstOrNull()
        val wishKey = prefs?.get(widget_wish_key) ?: ""
        provideContent {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFFB0E0E6))
                    .clickable(actionStartActivity(
                        Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                    ))
            ) {
                if (wishKey.isBlank()) {
                    EmptyKeyWidgetContent()
                } else {
                    WishContentWidget(wishKey = wishKey, context = context)
                }
            }
        }
    }
}

@Composable
private fun WishContentWidget(wishKey: String, context: Context) {
    val appContext = context.applicationContext ?: throw IllegalStateException()
    val entryPoint = EntryPointAccessors.fromApplication(
        appContext,
        WidgetGlance.WidgetGlanceEntryPoint::class.java
    )
    val repository = entryPoint.getWishRepository()
    val wishState = remember { mutableStateOf<UiState<Wish>>(UiState.Idle()) }
    LaunchedEffect(wishKey) {
        wishState.value = UiState.Loading()
        try {
            repository.getWishes(wishKey, 1).collect { uiState ->
                wishState.value = uiState
            }
        } catch (e: Exception) {
            wishState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    when (wishState.value) {
        is UiState.Loading -> {
            LoadingWidgetContent()
        }

        is UiState.Success -> {
            SuccessWidgetContent(
                wish = (wishState.value as UiState.Success).data,
                context = context
            )
        }

        is UiState.Error -> {
            ErrorWidgetContent(message = (wishState.value as UiState.Error).message)
        }

        is UiState.Idle -> {
            Text(text = "Idle State in Widget")
        }
    }
}


@Composable
private fun LoadingWidgetContent() {
    Column(
        modifier = GlanceModifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Загружаем..")
        Spacer(modifier = GlanceModifier.width(16.dp))
        CircularProgressIndicator()
    }
}

@Composable
private fun SuccessWidgetContent(
    wish: Wish,
    context: Context
) {
    val imageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
    val imageLoader = ImageLoader.Builder(context).build()

    val requestBuilder = ImageRequest.Builder(context)
        .data(wish.image)

    if (wish.isBlurred) {
        requestBuilder.transformations(
            listOf(
                BlurTransformation(
                    scale = 0.5f,
                    radius = 25
                )
            )
        )
    }

    val request = requestBuilder
        .listener(
            onSuccess = { _, result ->
                imageBitmap.value = result.drawable.toBitmap().asImageBitmap()
            },
            onError = { _, _ ->
                imageBitmap.value = null
            }
        )
        .build()

    LaunchedEffect(wish.image, wish.isBlurred) {
        imageLoader.enqueue(request)
    }
    Column(
        modifier = GlanceModifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = wish.user.name,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 4.dp),
            maxLines = 1
        )
        Text(
            text = wish.text,
            maxLines = 3,
            style = TextStyle(),
            modifier = GlanceModifier.padding(bottom = 8.dp)
        )
        imageBitmap.value?.let { bitmap ->
            Image(
                provider = ImageProvider(bitmap.asAndroidBitmap()),
                contentDescription = null,
                modifier = GlanceModifier.fillMaxSize().cornerRadius(16.dp),
                contentScale = ContentScale.Crop
            )
        } ?: run {
            Image(
                provider = ImageProvider(R.drawable.defaultprofilephoto),
                contentDescription = "Placeholder",
                modifier = GlanceModifier.fillMaxSize().cornerRadius(16.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ErrorWidgetContent(message: String?) {
    Column(
        modifier = GlanceModifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Ошибка загрузки пожелания", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = message ?: "Неизвестная ошибка")
    }
}


@Composable
private fun EmptyKeyWidgetContent() {
    Column(
        modifier = GlanceModifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Введите ключ в приложенни", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = "Чтобы увидеть пожелание")
    }
}

@AndroidEntryPoint
class SimpleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WidgetGlance()
}