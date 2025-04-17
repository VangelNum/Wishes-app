package com.vangelnum.wishes.features.home.sendwish.selectdate.wishkey.presentation

sealed class WishKeyEvent {
    data object OnGetWishKey: WishKeyEvent()
    data object OnRegenerateWishKey : WishKeyEvent()
}