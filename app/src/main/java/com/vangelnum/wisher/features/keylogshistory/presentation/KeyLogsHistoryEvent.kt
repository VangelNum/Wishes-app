package com.vangelnum.wisher.features.keylogshistory.presentation

sealed class KeyLogsHistoryEvent {
    data object OnGetKeyLogsHistory : KeyLogsHistoryEvent()
}