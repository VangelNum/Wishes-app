package com.vangelnum.wisher.features.auth.core.utils

import android.content.Context
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.presentation.SnackbarController
import com.vangelnum.wisher.core.presentation.SnackbarEvent
import com.vangelnum.wisher.core.utils.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun copyKey(
    clipboardManager: ClipboardManager,
    key: String,
    scope: CoroutineScope,
    context: Context
) {
    clipboardManager.setText(
        AnnotatedString(key)
    )
    scope.launch {
        SnackbarController.sendEvent(
            SnackbarEvent(
                message = string(context, R.string.copied)
            )
        )
    }
}