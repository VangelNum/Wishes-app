package com.vangelnum.wisher.features.userwishsendinghistory.presentation

sealed class UserWishesHistoryEvent {
    data object OnGetMyWishes: UserWishesHistoryEvent()
}