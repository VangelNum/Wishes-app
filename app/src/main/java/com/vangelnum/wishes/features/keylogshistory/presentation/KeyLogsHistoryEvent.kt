package com.vangelnum.wishes.features.keylogshistory.presentation

sealed class KeyLogsHistoryEvent {
    data object OnGetKeyLogsHistory : KeyLogsHistoryEvent()
}