package com.vangelnum.wisher

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vangelnum.wisher.presentation.LoginScreen
import com.vangelnum.wisher.presentation.RegistrationScreen
import com.vangelnum.wisher.presentation.RegistrationViewModel
import com.vangelnum.wisher.presentation.Screen


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Registration.route
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Registration.route) {
            val viewModel: RegistrationViewModel = hiltViewModel()
            RegistrationScreen(navController = navController, viewModel = viewModel)
        }
        composable(route = Screen.Login.route) {
//            LoginScreen(navController = navController)
        }
        composable(route = Screen.Profile.route) {
//            ProfileScreen()
        }
    }
}