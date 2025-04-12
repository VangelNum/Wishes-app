package com.vangelnum.wisher.features.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.vangelnum.wisher.App.Companion.dataStore
import kotlinx.coroutines.launch

@SuppressLint("MemberExtensionConflict")
@Composable
fun WidgetScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val widgetKey = remember { mutableStateOf(TextFieldValue("")) }
    val savedKey = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        context.dataStore.data.collect { prefs ->
            savedKey.value = prefs[WidgetKeys.WISH_KEY] ?: ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Введите ключ для виджета",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = widgetKey.value,
            onValueChange = { widgetKey.value = it },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
            onClick = {
                scope.launch {
                    context.dataStore.edit { prefs ->
                        prefs[WidgetKeys.WISH_KEY] = widgetKey.value.text
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

        ElevatedButton(
            onClick = {
                SimpleWidgetReceiver.restartWorker(context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
        ) {
            Text("Обновить виджет")
        }

        Spacer(modifier = Modifier.height(16.dp))

        @Suppress
        AnimatedVisibility(savedKey.value != "") {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Сохраненный ключ",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = savedKey.value,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

fun requestWidgetAddition(activity: Activity) {
    val appWidgetManager = AppWidgetManager.getInstance(activity)
    val myProvider = ComponentName(activity, SimpleWidgetReceiver::class.java)
    if (appWidgetManager.isRequestPinAppWidgetSupported) {
        appWidgetManager.requestPinAppWidget(myProvider, null, null)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewWidget() {
    WidgetScreen()
}