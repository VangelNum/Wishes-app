package com.vangelnum.wishes.features.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import com.vangelnum.wishes.App.Companion.dataStore
import com.vangelnum.wishes.ui.theme.WishesappTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WidgetConfigurationActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivity()

        setContent {
            val context = LocalContext.current
            val keysState = remember { mutableStateOf<Set<String>>(emptySet()) }

            LaunchedEffect(Unit) {
                context.dataStore.data.collect { prefs ->
                    keysState.value = prefs[WidgetKeys.WIDGET_KEYS_SET] ?: emptySet()
                }
            }

            WishesappTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Выберите ключ для виджета") })
                    }
                ) { padding ->
                    if (keysState.value.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(padding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Сначала добавьте ключи на экране виджетов в приложении.")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                            items(keysState.value.toList()) { key ->
                                Text(
                                    text = key,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onKeySelected(key) }
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupActivity() {
        setResult(RESULT_CANCELED)
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
    }

    private fun onKeySelected(key: String) {
        val context = this
        lifecycleScope.launch {
            Log.d("WidgetConfig", "Saving key '$key' for appWidgetId = $appWidgetId")

            val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)

            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[WidgetKeys.wishKeyFor(appWidgetId.toString())] = key
            }

            SimpleWidgetReceiver.restartWorker(context)

            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}