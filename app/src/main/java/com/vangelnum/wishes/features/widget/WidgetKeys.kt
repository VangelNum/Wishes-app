package com.vangelnum.wishes.features.widget

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetKeys {
    val WISH_KEY = stringPreferencesKey("wish_key")
    val WISH_INFO = stringPreferencesKey("wish_info")
    val WISH_IMAGE = stringPreferencesKey("wish_image")
    val WISH_SENDER = stringPreferencesKey("wish_sender")
    val WISH_IS_BLURRED = booleanPreferencesKey("wish_blur")
    val IS_LOADING = booleanPreferencesKey("is_loading")
    val ERROR_MESSAGE = stringPreferencesKey("error_message")
}