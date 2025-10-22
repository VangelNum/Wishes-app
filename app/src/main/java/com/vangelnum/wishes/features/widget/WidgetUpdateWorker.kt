package com.vangelnum.wishes.features.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.home.getwish.domain.repository.GetWishRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getWishInfoRepository: GetWishRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val glanceManager = GlanceAppWidgetManager(appContext)
        val glanceIds = try {
            glanceManager.getGlanceIds(WidgetGlance::class.java)
        } catch (e: Exception) {
            Log.e("WidgetUpdateWorker", "Failed to get glanceIds", e)
            return Result.failure()
        }

        Log.d("WidgetUpdateWorker", "doWork: Found glanceIds: $glanceIds")

        if (glanceIds.isEmpty()) {
            return Result.success()
        }

        glanceIds.forEach { glanceId ->
            val currentAppWidgetId = try {
                glanceManager.getAppWidgetId(glanceId)
            } catch (e: IllegalStateException) {
                // Может произойти, если виджет был удален во время работы worker'а
                Log.w("WidgetUpdateWorker", "Could not find appWidgetId for glanceId $glanceId, widget might be deleted. Skipping.")
                return@forEach
            }

            Log.d("WidgetUpdateWorker", "Processing appWidgetId: $currentAppWidgetId")

            val prefs = getAppWidgetState(appContext, PreferencesGlanceStateDefinition, glanceId)
            val currentWishKey = prefs[WidgetKeys.wishKeyFor(currentAppWidgetId.toString())] // Используем appWidgetId для ключа

            if (currentWishKey.isNullOrEmpty()) {
                Log.w("WidgetUpdateWorker", "No wishKey for appWidgetId $currentAppWidgetId, skipping.")
                return@forEach
            }

            Log.d("WidgetUpdateWorker", "Setting loading state for appWidgetId $currentAppWidgetId")
            updateAppWidgetState(appContext, glanceId) { mutablePrefs ->
                mutablePrefs[WidgetKeys.isLoadingFor(currentAppWidgetId.toString())] = true
                mutablePrefs.remove(WidgetKeys.errorMessageFor(currentAppWidgetId.toString()))
            }
            WidgetGlance().update(appContext, glanceId)

            val wishState = getWishInfoRepository.getLastWishByKey(currentWishKey)
                .first { it !is UiState.Loading }

            Log.d("WidgetUpdateWorker", "Setting final state for appWidgetId $currentAppWidgetId")
            updateAppWidgetState(appContext, glanceId) { mutablePrefs ->
                mutablePrefs[WidgetKeys.isLoadingFor(currentAppWidgetId.toString())] = false
                when (wishState) {
                    is UiState.Success -> {
                        val wish = wishState.data
                        mutablePrefs[WidgetKeys.wishKeyFor(currentAppWidgetId.toString())] = currentWishKey
                        mutablePrefs[WidgetKeys.wishInfoFor(currentAppWidgetId.toString())] = wish.text
                        mutablePrefs[WidgetKeys.wishSenderFor(currentAppWidgetId.toString())] = wish.user.name
                        mutablePrefs[WidgetKeys.wishImageFor(currentAppWidgetId.toString())] = wish.image
                        mutablePrefs[WidgetKeys.wishIsBlurredFor(currentAppWidgetId.toString())] = wish.isBlurred
                        mutablePrefs.remove(WidgetKeys.errorMessageFor(currentAppWidgetId.toString()))
                    }
                    is UiState.Error -> {
                        mutablePrefs[WidgetKeys.errorMessageFor(currentAppWidgetId.toString())] = wishState.message
                    }
                    else -> { /* No-op */ }
                }
            }
            WidgetGlance().update(appContext, glanceId)
        }
        return Result.success()
    }
}