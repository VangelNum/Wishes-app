package com.vangelnum.wisher.features.userviewhistory.presentation

sealed class ViewHistoryEvent {
    data class OnGetViewHistory(val wishId: Int): ViewHistoryEvent()
}