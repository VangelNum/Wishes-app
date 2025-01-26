package com.vangelnum.wisher.features.userwisheshistory.presentation

sealed class UserWishesHistoryEvent {
    data object OnGetMyWishes: UserWishesHistoryEvent()
}