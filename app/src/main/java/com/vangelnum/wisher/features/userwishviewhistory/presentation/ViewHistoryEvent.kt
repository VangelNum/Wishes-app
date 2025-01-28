package com.vangelnum.wisher.features.userwishviewhistory.presentation

sealed class ViewHistoryEvent {
    data class OnGetViewHistory(val wishId: Int): ViewHistoryEvent()
}