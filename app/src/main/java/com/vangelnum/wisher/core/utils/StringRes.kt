package com.vangelnum.wisher.core.utils

import android.content.Context
import androidx.annotation.StringRes

fun string(context: Context, id: Int): String {
    return context.getString(id)
}

fun string(
    context: Context,
    @StringRes id: Int,
    vararg formatArgs: Any
): String {
    return context.getString(id, *formatArgs)
}