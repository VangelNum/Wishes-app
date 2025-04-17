package com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.data.model

data class DateInfo(
    val day: Int,
    val formatted: String,
    val hour: Int,
    val minute: Int,
    val month: Int,
    val timestamp: Long,
    val timezone: String,
    val weekDay: Int,
    val year: Int
) {
    override fun toString(): String {
        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }
}