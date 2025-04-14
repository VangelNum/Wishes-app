package com.vangelnum.wisher.features.auth.core.utils

import android.content.Context
import android.content.Intent

fun shareKey(key: String, context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, key)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share Key")
    context.startActivity(shareIntent)
}