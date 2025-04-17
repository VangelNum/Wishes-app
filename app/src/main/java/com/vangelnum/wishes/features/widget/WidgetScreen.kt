package com.vangelnum.wishes.features.widget

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.vangelnum.wishes.App.Companion.dataStore
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.presentation.SnackbarController
import com.vangelnum.wishes.core.presentation.SnackbarEvent
import com.vangelnum.wishes.core.utils.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val widgetKey = remember { mutableStateOf(TextFieldValue("")) }
    val savedKey = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        context.dataStore.data.collect { prefs ->
            val keyFromDataStore = prefs[WidgetKeys.WISH_KEY] ?: ""
            savedKey.value = keyFromDataStore
            if (widgetKey.value.text.isEmpty()) {
                widgetKey.value = TextFieldValue(keyFromDataStore)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.widget_screen_instruction),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = widgetKey.value,
            onValueChange = { widgetKey.value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.widget_screen_key_label)) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        ElevatedButton(
            onClick = {
                val newKey = widgetKey.value.text
                scope.launch {
                    context.dataStore.edit { prefs ->
                        prefs[WidgetKeys.WISH_KEY] = newKey
                    }
                }
                SimpleWidgetReceiver.restartWorker(context)
            },
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_save_24),
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = stringResource(R.string.widget_screen_save_key_button))
        }

        FilledTonalButton(
            onClick = {
                requestWidgetAddition(context as Activity, scope)
            },
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                painterResource(R.drawable.baseline_widgets_24),
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = stringResource(R.string.widget_screen_add_widget_button))
        }

        FilledTonalButton(
            onClick = {
                SimpleWidgetReceiver.restartWorker(context)
            },
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                painterResource(R.drawable.baseline_sync_24),
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(stringResource(R.string.widget_screen_update_widget_button))
        }

        AnimatedVisibility(
            visible = savedKey.value.isNotEmpty(),
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = stringResource(R.string.widget_screen_saved_key_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    SelectionContainer {
                        Text(
                            text = savedKey.value,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

fun requestWidgetAddition(activity: Activity, scope: CoroutineScope) {
    val appWidgetManager = AppWidgetManager.getInstance(activity)
    val myProvider = ComponentName(activity, SimpleWidgetReceiver::class.java)

    if (appWidgetManager.isRequestPinAppWidgetSupported) {
        val successIntent = Intent(activity, WidgetAddedReceiver::class.java).apply {
            action = WidgetAddedReceiver.ACTION_WIDGET_ADDED_CALLBACK
            component = ComponentName(activity, WidgetAddedReceiver::class.java)
        }
        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val successCallback = PendingIntent.getBroadcast(
            activity,
            0,
            successIntent,
            pendingIntentFlags
        )

        try {
            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
        } catch (e: SecurityException) {
            Log.e("WidgetRequest", "SecurityException requesting pin widget", e)
            scope.launch {
                SnackbarController.sendEvent(SnackbarEvent("Не удалось запросить добавление виджета: ${e.localizedMessage}"))
            }
        } catch (e: Exception) {
            Log.e("WidgetRequest", "Exception requesting pin widget", e)
            scope.launch {
                SnackbarController.sendEvent(SnackbarEvent("Ошибка при запросе добавления виджета: ${e.localizedMessage}"))
            }
        }

    } else {
        scope.launch {
            SnackbarController.sendEvent(SnackbarEvent(string(activity, R.string.widget_screen_pin_widget_error)))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewWidget() {
    WidgetScreen()
}