package com.vangelnum.wisher

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest
import com.vangelnum.wisher.features.auth.presentation.LoginEvent
import com.vangelnum.wisher.features.auth.presentation.LoginScreen
import com.vangelnum.wisher.features.auth.presentation.LoginViewModel
import com.vangelnum.wisher.features.auth.presentation.RegistrationEvent
import com.vangelnum.wisher.features.auth.presentation.RegistrationScreen
import com.vangelnum.wisher.features.auth.presentation.RegistrationViewModel
import com.vangelnum.wisher.features.home.HomeEvent
import com.vangelnum.wisher.features.home.HomeScreen
import com.vangelnum.wisher.features.home.HomeViewModel


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = LoginPage
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val loginState = loginViewModel.loginUiState.collectAsState().value
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<RegistrationPage> {
            val registrationViewModel: RegistrationViewModel = hiltViewModel()
            val registrationState =
                registrationViewModel.registrationState.collectAsStateWithLifecycle().value
            RegistrationScreen(
                registrationState = registrationState,
                onBackToEmptyState = {
                    registrationViewModel.onEvent(RegistrationEvent.OnBackToEmptyState)
                },
                onNavigateToMainScreen = {
                    navController.navigate(HomePage)
                },
                onRegisterUser = { name, email, password ->
                    registrationViewModel.onEvent(
                        RegistrationEvent.OnRegisterUser(
                            RegistrationRequest(name = name, email = email, password = password)
                        )
                    )
                },
                onNavigateToLoginPage = {
                    navController.navigate(LoginPage)
                }
            )
        }
        composable<LoginPage> {
            LoginScreen(
                loginState = loginState,
                onBackToEmptyState = {
                    loginViewModel.onEvent(LoginEvent.onBackToEmptyState)
                },
                onNavigateToHomeScreen = {
                    navController.navigate(HomePage)
                },
                onLoginUser = { email, password ->
                    loginViewModel.onEvent(LoginEvent.onLoginUser(email, password))
                },
                onNavigateToRegisterScreen = {
                    navController.navigate(RegistrationPage)
                }
            )
        }
        composable<ProfilePage> {
            Text("Profile page")
        }
        composable<HomePage> {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val homeKeyUiState = homeViewModel.homeKeyUiState.collectAsStateWithLifecycle().value
            HomeScreen(homeKeyUiState = homeKeyUiState, onGetWishKey = {
                homeViewModel.onEvent(HomeEvent.onGetWishKey)
            })
        }
    }
}