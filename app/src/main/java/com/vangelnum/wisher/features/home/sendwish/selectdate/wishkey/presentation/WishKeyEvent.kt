package com.vangelnum.wisher.features.home.sendwish.selectdate.wishkey.presentation

sealed class WishKeyEvent {
    data object OnGetWishKey: WishKeyEvent()
    data object OnRegenerateWishKey : WishKeyEvent()
}