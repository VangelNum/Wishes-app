package com.vangelnum.wishes.features.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WidgetAddedReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_WIDGET_ADDED_CALLBACK = "com.vangelnum.wishes.ACTION_WIDGET_ADDED_CALLBACK"
        const val EXTRA_WIDGET_KEY = "WIDGET_KEY"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.action != ACTION_WIDGET_ADDED_CALLBACK) {
            return
        }

        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        val wishKey = intent.getStringExtra(EXTRA_WIDGET_KEY)

        Log.d("WidgetAddedReceiver", "Received callback. appWidgetId=$appWidgetId, wishKey=$wishKey")

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID || wishKey.isNullOrEmpty()) {
            Log.e("WidgetAddedReceiver", "Invalid data received, aborting.")
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)

                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[WidgetKeys.wishKeyFor(appWidgetId.toString())] = wishKey
                }

                Log.d("WidgetAddedReceiver", "Successfully saved key '$wishKey' for appWidgetId=$appWidgetId")

                WidgetGlance().update(context, glanceId)

                SimpleWidgetReceiver.restartWorker(context.applicationContext)

            } catch (e: Exception) {
                Log.e("WidgetAddedReceiver", "Error saving widget state", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}