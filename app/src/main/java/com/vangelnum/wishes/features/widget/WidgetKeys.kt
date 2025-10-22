package com.vangelnum.wishes.features.widget

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object WidgetKeys {
    val WIDGET_KEYS_SET = stringSetPreferencesKey("widget_keys_set")
    fun wishKeyFor(appWidgetIdStr: String) = stringPreferencesKey("wish_key_$appWidgetIdStr")
    fun wishInfoFor(appWidgetIdStr: String) = stringPreferencesKey("wish_info_$appWidgetIdStr")
    fun wishImageFor(appWidgetIdStr: String) = stringPreferencesKey("wish_image_$appWidgetIdStr")
    fun wishSenderFor(appWidgetIdStr: String) = stringPreferencesKey("wish_sender_$appWidgetIdStr")
    fun wishIsBlurredFor(appWidgetIdStr: String) = booleanPreferencesKey("wish_blur_$appWidgetIdStr")
    fun isLoadingFor(appWidgetIdStr: String) = booleanPreferencesKey("is_loading_$appWidgetIdStr")
    fun errorMessageFor(appWidgetIdStr: String) = stringPreferencesKey("error_message_$appWidgetIdStr")
    fun appWidgetIdToGlanceIdKey(appWidgetId: Int) = stringPreferencesKey("map_appwidgetid_to_glanceid_$appWidgetId")
}