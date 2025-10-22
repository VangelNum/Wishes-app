package com.vangelnum.wishes

import kotlinx.serialization.Serializable

@Serializable
object RegistrationPage

@Serializable
object LoginPage

@Serializable
object ProfilePage

@Serializable
data class HomePage(
    val key: String? = null,
    val selectedTab: Int? = null
)

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

@Serializable
object KeyLogsHistoryPage

@Serializable
object WidgetPage

@Serializable
object BunsPage

@Serializable
object BonusPage

@Serializable
data class EditProfilePage(
    val avatar: String,
    val name: String,
    val email: String
)