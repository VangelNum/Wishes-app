package com.vangelnum.wishes.features.widget

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.vangelnum.wishes.App.Companion.dataStore
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.presentation.SnackbarController
import com.vangelnum.wishes.core.presentation.SnackbarEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun WidgetScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keysState = remember { mutableStateOf<Set<String>>(emptySet()) }
    val newKeyInput = remember { mutableStateOf(TextFieldValue("")) }
    val activity = context.findActivity()

    LaunchedEffect(Unit) {
        context.dataStore.data.collect { prefs ->
            keysState.value = prefs[WidgetKeys.WIDGET_KEYS_SET] ?: emptySet()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.widget_screen_info_text),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.widget_screen_create_new_key_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    OutlinedTextField(
                        value = newKeyInput.value,
                        onValueChange = { newKeyInput.value = it },
                        label = { Text(stringResource(R.string.widget_screen_key_label)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    FilledTonalButton(
                        onClick = {
                            val newKey = newKeyInput.value.text.trim()
                            if (newKey.isNotEmpty() && !keysState.value.contains(newKey)) {
                                scope.launch {
                                    context.dataStore.edit { prefs ->
                                        val currentKeys =
                                            prefs[WidgetKeys.WIDGET_KEYS_SET] ?: emptySet()
                                        prefs[WidgetKeys.WIDGET_KEYS_SET] = currentKeys + newKey
                                    }
                                    newKeyInput.value = TextFieldValue("")
                                }
                            }
                        },
                        modifier = Modifier.height(OutlinedTextFieldDefaults.MinHeight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.widget_screen_add_button_description)
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        if (keysState.value.isEmpty()) {
            Text(
                text = stringResource(R.string.widget_screen_no_keys_added),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Text(
                text = stringResource(R.string.widget_screen_your_keys_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
            keysState.value.forEach { key ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SelectionContainer(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    context.dataStore.edit { prefs ->
                                        val currentKeys =
                                            prefs[WidgetKeys.WIDGET_KEYS_SET] ?: emptySet()
                                        prefs[WidgetKeys.WIDGET_KEYS_SET] = currentKeys - key
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(
                                        R.string.widget_screen_delete_key_description,
                                        key
                                    ),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = {
                                if (activity != null) {
                                    requestWidgetAddition(activity, scope, key)
                                } else {
                                    scope.launch {
                                        SnackbarController.sendEvent(
                                            SnackbarEvent(
                                                context.getString(R.string.widget_screen_activity_not_found_error)
                                            )
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_widgets_24),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.widget_screen_add_widget_button_text),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun requestWidgetAddition(activity: Activity, scope: CoroutineScope, key: String) {
    val appWidgetManager = AppWidgetManager.getInstance(activity)
    val myProvider = ComponentName(activity, SimpleWidgetReceiver::class.java)

    if (appWidgetManager.isRequestPinAppWidgetSupported) {
        val successIntent = Intent(activity, WidgetAddedReceiver::class.java).apply {
            action = WidgetAddedReceiver.ACTION_WIDGET_ADDED_CALLBACK
            putExtra(WidgetAddedReceiver.EXTRA_WIDGET_KEY, key)
        }

        val requestCode = key.hashCode()
        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE

        val successCallback = PendingIntent.getBroadcast(
            activity,
            requestCode,
            successIntent,
            pendingIntentFlags
        )

        try {
            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
        } catch (e: Exception) {
            Log.e("WidgetRequest", "Exception requesting pin widget", e)
            scope.launch {
                val message = activity.getString(
                    R.string.widget_screen_request_pin_widget_exception,
                    e.localizedMessage ?: ""
                )
                SnackbarController.sendEvent(SnackbarEvent(message))
            }
        }
    } else {
        scope.launch {
            SnackbarController.sendEvent(
                SnackbarEvent(activity.getString(R.string.widget_screen_pin_widget_not_supported_error))
            )
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}