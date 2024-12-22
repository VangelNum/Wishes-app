package com.vangelnum.wisher.presentation

sealed class Screen(val route: String) {
    object Registration : Screen("registration")
    object Login : Screen("login")
    object Profile : Screen("profile")
}