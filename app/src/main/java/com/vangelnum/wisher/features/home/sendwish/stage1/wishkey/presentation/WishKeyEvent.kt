package com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.presentation

sealed class WishKeyEvent {
    data object OnGetWishKeyKey: WishKeyEvent()
    data object OnGetDate : WishKeyEvent()
    data object OnRegenerateWishKey : WishKeyEvent()
}