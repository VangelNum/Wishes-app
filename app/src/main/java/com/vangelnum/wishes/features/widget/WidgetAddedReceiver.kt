package com.vangelnum.wishes.features.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WidgetAddedReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_WIDGET_ADDED_CALLBACK = "com.vangelnum.wishes.ACTION_WIDGET_ADDED_CALLBACK"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("WidgetAddedReceiver", "Received broadcast: ${intent?.action}")
        if (context != null && intent?.action == ACTION_WIDGET_ADDED_CALLBACK) {
            Log.d("WidgetAddedReceiver", "Widget successfully added callback received. Restarting worker.")
            SimpleWidgetReceiver.restartWorker(context.applicationContext)
        }
    }
}