package com.vangelnum.wisher.features.home.getwish.presentation

sealed class GetWishEvent {
    data class OnGetWishesDates(val key: String): GetWishEvent()
    data class OnGetWishes(val key: String, val id: Int): GetWishEvent()
}