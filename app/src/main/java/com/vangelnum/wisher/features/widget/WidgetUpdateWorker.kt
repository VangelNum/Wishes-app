package com.vangelnum.wisher.features.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vangelnum.wisher.App.Companion.dataStore
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.getwish.domain.repository.GetWishRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getWishInfoRepository: GetWishRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(WidgetGlance::class.java)

        if (glanceIds.isEmpty()) {
            Log.d("WidgetUpdateWorker", "No Glance widgets found, finishing worker.")
            return Result.failure()
        }
        Log.d("WidgetUpdateWorker", "Starting worker, updating widgets: $glanceIds")

        val wishKey = runBlocking {
            context.dataStore.data.first()[WidgetKeys.WISH_KEY] ?: ""
        }

        if (wishKey.isEmpty()) {
            for (glanceId in glanceIds) {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[WidgetKeys.IS_LOADING] = false
                        this[WidgetKeys.ERROR_MESSAGE] = "Ключ виджета не установлен"
                        remove(WidgetKeys.WISH_INFO)
                        remove(WidgetKeys.WISH_IMAGE)
                        remove(WidgetKeys.WISH_SENDER)
                    }
                }
                WidgetGlance().update(context, glanceId)
            }
            return Result.failure()
        }

        for (glanceId in glanceIds) {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[WidgetKeys.IS_LOADING] = true
                    remove(WidgetKeys.ERROR_MESSAGE)
                    remove(WidgetKeys.WISH_INFO)
                    remove(WidgetKeys.WISH_IMAGE)
                    remove(WidgetKeys.WISH_SENDER)
                    remove(WidgetKeys.WISH_IS_BLURRED)
                }
            }
            WidgetGlance().update(context, glanceId)
        }

        var result: Result = Result.failure()
        getWishInfoRepository.getLastWishByKey(wishKey).collect { wishState ->
            when (wishState) {
                is UiState.Success -> {
                    val wish = wishState.data
                    for (glanceId in glanceIds) {
                        updateAppWidgetState(
                            context,
                            PreferencesGlanceStateDefinition,
                            glanceId
                        ) { prefs ->
                            prefs.toMutablePreferences().apply {
                                this[WidgetKeys.WISH_INFO] = wish.text
                                this[WidgetKeys.WISH_SENDER] = wish.user.name
                                this[WidgetKeys.WISH_IMAGE] = wish.image
                                this[WidgetKeys.WISH_IS_BLURRED] = wish.isBlurred
                                this[WidgetKeys.IS_LOADING] = false
                                remove(WidgetKeys.ERROR_MESSAGE)
                            }
                        }
                        WidgetGlance().update(context, glanceId)
                    }
                    Log.d("WidgetUpdateWorker", "Successfully updated widget with wish data")
                    result = Result.success()
                    return@collect
                }

                is UiState.Error -> {
                    val errorMessage = wishState.message
                    for (glanceId in glanceIds) {
                        updateAppWidgetState(
                            context,
                            PreferencesGlanceStateDefinition,
                            glanceId
                        ) { prefs ->
                            prefs.toMutablePreferences().apply {
                                this[WidgetKeys.ERROR_MESSAGE] = errorMessage
                                this[WidgetKeys.IS_LOADING] = false
                                remove(WidgetKeys.WISH_INFO)
                                remove(WidgetKeys.WISH_IMAGE)
                                remove(WidgetKeys.WISH_SENDER)
                                remove(WidgetKeys.WISH_IS_BLURRED)
                            }
                        }
                        WidgetGlance().update(context, glanceId)
                    }
                    Log.e("WidgetUpdateWorker", "Error fetching wish data: $errorMessage")
                    result = Result.failure()
                    return@collect
                }

                is UiState.Loading -> {}

                is UiState.Idle -> {
                    result = Result.failure()
                    return@collect
                }
            }
        }
        return result
    }
}