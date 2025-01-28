package com.vangelnum.wisher.features.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.vangelnum.wisher.App.Companion.dataStore
import kotlinx.coroutines.launch

@Composable
fun WidgetScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val widgetKey = remember { mutableStateOf(TextFieldValue("")) }
    val savedKey = remember { mutableStateOf("") }
    val widgetWishKey = stringPreferencesKey("widget_wish_key")

    LaunchedEffect(Unit) {
        context.dataStore.data.collect { prefs ->
            savedKey.value = prefs[widgetWishKey] ?: ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Введите ключ для виджета:",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = widgetKey.value,
            onValueChange = { widgetKey.value = it },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
            onClick = {
                scope.launch {
                    context.dataStore.edit { prefs ->
                        prefs[widgetWishKey] = widgetKey.value.text
                    }
                    val glanceAppWidgetManager = GlanceAppWidgetManager(context)
                    val glanceIds = glanceAppWidgetManager.getGlanceIds(WidgetGlance::class.java)
                    glanceIds.forEach { glanceId ->
                        WidgetGlance().update(context, glanceId)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
        ) {
            Text(text = "Сохранить ключ")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
            onClick = {
                requestWidgetAddition(context as Activity)
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
        ) {
            Text(text = "Добавить виджет")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Сохраненный ключ: ${savedKey.value}",
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

fun requestWidgetAddition(activity: Activity) {
    val appWidgetManager = AppWidgetManager.getInstance(activity)
    val myProvider = ComponentName(activity, SimpleWidgetReceiver::class.java)
    if (appWidgetManager.isRequestPinAppWidgetSupported) {
        appWidgetManager.requestPinAppWidget(myProvider, null, null)
    }
}