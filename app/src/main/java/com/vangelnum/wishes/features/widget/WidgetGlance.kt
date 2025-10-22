package com.vangelnum.wishes.features.widget

import android.content.Context
import android.content.Intent
import android.util.Log
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
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.ImageLoader
import coil.request.ImageRequest
import com.vangelnum.wishes.App.Companion.dataStore
import com.vangelnum.wishes.MainActivity
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.utils.string
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class WidgetGlance : GlanceAppWidget() {
    override var stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val appWidgetIdStr = appWidgetId.toString()

        provideContent {
            val prefs = currentState<Preferences>()
            val wishKey = prefs[WidgetKeys.wishKeyFor(appWidgetIdStr)]
            val wishText = prefs[WidgetKeys.wishInfoFor(appWidgetIdStr)]
            val wishSender = prefs[WidgetKeys.wishSenderFor(appWidgetIdStr)]
            val wishImage = prefs[WidgetKeys.wishImageFor(appWidgetIdStr)]
            val wishIsBlurred = prefs[WidgetKeys.wishIsBlurredFor(appWidgetIdStr)] ?: false
            val isLoading = prefs[WidgetKeys.isLoadingFor(appWidgetIdStr)] ?: false
            val errorMessage = prefs[WidgetKeys.errorMessageFor(appWidgetIdStr)]

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFFB0E0E6))
                    .clickable(
                        actionStartActivity(
                            Intent(context, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                if (wishKey != null && wishKey.isNotBlank()) {
                                    putExtra("wishKeyFromWidget", wishKey)
                                }
                            }
                        )
                    )
            ) {
                Column(
                    modifier = GlanceModifier.fillMaxSize().padding(4.dp)
                ) {
                    Box(modifier = GlanceModifier.fillMaxWidth().defaultWeight().padding(4.dp)) {
                        if (isLoading) {
                            LoadingWidgetContent(context)
                        }
                        else if (wishKey.isNullOrBlank()) {
                            EmptyKeyWidgetContent(context)
                        }
                        else if (errorMessage != null) {
                            ErrorWidgetContent(context, errorMessage)
                        }
                        else if (wishImage != null && wishSender != null && wishText != null) {
                            SuccessWidgetContent(
                                wishSender,
                                wishText,
                                wishImage,
                                wishIsBlurred,
                                context
                            )
                        }
                    }
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Button(
                            text = string(context, R.string.refresh),
                            onClick = actionRunCallback<RefreshWidgetCallback>(),
                            modifier = GlanceModifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyKeyWidgetContent(context: Context) {
    Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(string(context, R.string.wish_key_not_setup))
    }
}

@Composable
fun SuccessWidgetContent(
    wishSender: String,
    wishText: String,
    wishImage: String,
    wishIsBlurred: Boolean,
    context: Context
) {

    val imageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
    val imageLoader = ImageLoader.Builder(context).build()

    val requestBuilder = ImageRequest.Builder(context)
        .data(wishImage)

    if (wishIsBlurred) {
        requestBuilder.transformations(
            listOf(
                BlurTransformation(
                    scale = 0.5f,
                    radius = 50
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

    LaunchedEffect(wishImage, wishIsBlurred) {
        imageLoader.enqueue(request)
    }

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = wishSender,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 4.dp),
            maxLines = 1
        )
        Text(
            text = wishText,
            maxLines = 4,
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
fun LoadingWidgetContent(context: Context) {
    Column(
        modifier = GlanceModifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            string(context, R.string.loading)
        )
        Spacer(modifier = GlanceModifier.width(16.dp))
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorWidgetContent(
    context: Context,
    message: String?
) {
    Column(
        modifier = GlanceModifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message ?: string(context, R.string.unknown_error)
        )
    }
}

class RefreshWidgetCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        SimpleWidgetReceiver.restartWorker(context)
    }
}

@AndroidEntryPoint
class SimpleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: WidgetGlance = WidgetGlance()

    companion object {
        const val WORK_NAME = "WidgetUpdateWorker"

        fun restartWorker(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(WORK_NAME)
            startWorker(context)
        }

        private fun startWorker(context: Context) {
            val periodicWorkRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                15,
                TimeUnit.MINUTES
            ).addTag(WORK_NAME).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
            )
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        startWorker(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_NAME)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Log.d("SimpleWidgetReceiver", "onDeleted called for appWidgetIds: ${appWidgetIds.joinToString()}")
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            for (appWidgetId in appWidgetIds) {
                val appWidgetIdStr = appWidgetId.toString()
                context.dataStore.edit { mutablePrefs ->
                    mutablePrefs.remove(WidgetKeys.wishKeyFor(appWidgetIdStr))
                    mutablePrefs.remove(WidgetKeys.wishInfoFor(appWidgetIdStr))
                    mutablePrefs.remove(WidgetKeys.wishImageFor(appWidgetIdStr))
                    mutablePrefs.remove(WidgetKeys.wishSenderFor(appWidgetIdStr))
                    mutablePrefs.remove(WidgetKeys.wishIsBlurredFor(appWidgetIdStr))
                    mutablePrefs.remove(WidgetKeys.isLoadingFor(appWidgetIdStr))
                    mutablePrefs.remove(WidgetKeys.errorMessageFor(appWidgetIdStr))
                }
            }
        }
    }
}