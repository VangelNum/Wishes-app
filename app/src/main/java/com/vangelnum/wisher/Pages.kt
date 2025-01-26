package com.vangelnum.wisher

import kotlinx.serialization.Serializable

@Serializable
object RegistrationPage

@Serializable
object LoginPage

@Serializable
object ProfilePage

@Serializable
object HomePage

@Serializable
data class UploadAvatarPage(
    val email: String,
    val password: String
)

@Serializable
data class HolidaysPage(
    val holidayDate: String,
    val key: String,
    val currentDate: String
)

@Serializable
data class VerifyEmailPage(
    val email: String,
    val password: String,
)

@Serializable
data class SendWishPage(
    val holidayDate: String,
    val currentDate: String,
    val key: String,
    val holidayName: String
)

@Serializable
object UserWishesHistoryPage

@Serializable
data class ViewHistoryPage(val wishId: Int)