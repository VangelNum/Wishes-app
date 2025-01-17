package com.vangelnum.wisher.features.home.sendwish.stage2.presentation

sealed class HolidaysEvent {
    data class GetHolidays(val date: String) : HolidaysEvent()
}