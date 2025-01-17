package com.vangelnum.wisher

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.presentation.login.LoginEvent
import com.vangelnum.wisher.features.auth.presentation.login.LoginScreen
import com.vangelnum.wisher.features.auth.presentation.login.LoginViewModel
import com.vangelnum.wisher.features.auth.presentation.registration.stage1.RegistrationScreen
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.presentation.LoadAvatarScreen
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.presentation.UploadAvatarViewModel
import com.vangelnum.wisher.features.home.HomeScreen
import com.vangelnum.wisher.features.home.getwish.presentation.GetWishViewModel
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.presentation.WishKeyEvent
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.presentation.WishKeyViewModel
import com.vangelnum.wisher.features.home.sendwish.stage2.presentation.HolidaysEvent
import com.vangelnum.wisher.features.home.sendwish.stage2.presentation.HolidaysScreen
import com.vangelnum.wisher.features.home.sendwish.stage2.presentation.HolidaysViewModel
import com.vangelnum.wisher.features.home.sendwish.stage3.presentation.SendWishEvent
import com.vangelnum.wisher.features.home.sendwish.stage3.presentation.SendWishScreen
import com.vangelnum.wisher.features.home.sendwish.stage3.presentation.SendWishViewModel


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any = LoginPage,
    loginViewModel: LoginViewModel,
    loginState: UiState<AuthResponse>,
    registrationViewModel: UploadAvatarViewModel,
    registrationState: UiState<AuthResponse>
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<RegistrationPage> {
            RegistrationScreen(
                onNavigateToLoginPage = {
                    navController.navigate(LoginPage)
                },
                onNavigateToUploadAvatarScreen = { name, email, password ->
                    navController.navigate(
                        UploadAvatarPage(
                            name, email, password
                        )
                    )
                }
            )
        }
        composable<LoginPage> {
            LoginScreen(
                loginState = loginState,
                onBackToEmptyState = {
                    loginViewModel.onEvent(LoginEvent.OnBackToEmptyState)
                },
                onNavigateToHomeScreen = {
                    navController.navigate(HomePage)
                },
                onLoginUser = { email, password ->
                    loginViewModel.onEvent(LoginEvent.OnLoginUser(email, password))
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
            val wishKeyViewModel: WishKeyViewModel = hiltViewModel()
            val wishesViewModel: GetWishViewModel = hiltViewModel()
            val wishesState = wishesViewModel.wishesState.collectAsStateWithLifecycle().value
            val homeKeyUiState = wishKeyViewModel.homeKeyUiState.collectAsStateWithLifecycle().value
            val dateUiState = wishKeyViewModel.dateUiState.collectAsStateWithLifecycle().value
            HomeScreen(homeKeyUiState = homeKeyUiState, currentDateUiState = dateUiState, onGetWishKey = {
                wishKeyViewModel.onEvent(WishKeyEvent.OnGetWishKeyKey)
            }, onGetTime = {
                wishKeyViewModel.onEvent(WishKeyEvent.OnGetDate)
            }, onNavigateHolidaysScreen = { holidayDate, key, currentDate ->
                navController.navigate(HolidaysPage(holidayDate, key, currentDate))
            }, wishesState = wishesState, onGetWishes = { key->
                wishesViewModel.getWishes(key)
            })
        }
        composable<UploadAvatarPage> { back ->
            val args = back.toRoute<UploadAvatarPage>()
            val uploadAvatarState =
                registrationViewModel.uploadAvatarState.collectAsStateWithLifecycle().value
            LoadAvatarScreen(
                name = args.name,
                email = args.email,
                password = args.password,
                registrationState = registrationState,
                uploadAvatarState = uploadAvatarState,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onEvent = { event ->
                    registrationViewModel.onEvent(event)
                },
                onNavigateToHome = {
                    navController.navigate(HomePage) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable<HolidaysPage> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<HolidaysPage>()
            val holidaysViewModel = hiltViewModel<HolidaysViewModel>()
            val holidaysState = holidaysViewModel.uiState.collectAsStateWithLifecycle().value
            LaunchedEffect(true) {
                holidaysViewModel.onEvent(HolidaysEvent.GetHolidays(args.holidayDate))
            }
            HolidaysScreen(
                holidayDate = args.holidayDate,
                key = args.key,
                currentDate = args.currentDate,
                holidaysState = holidaysState,
                onTryAgainLoadingHolidays = {
                    holidaysViewModel.onEvent(HolidaysEvent.GetHolidays(args.holidayDate))
                },
                onContinueClick = { holidayDate, key, holiday, currentDate ->
                    navController.navigate(SendWishPage(holidayDate = holidayDate, key = key, holidayName = holiday.name, currentDate = currentDate))
                }
            )
        }
        composable<SendWishPage> { backStackEntry ->
            val args = backStackEntry.toRoute<SendWishPage>()
            val sendWishViewModel = hiltViewModel<SendWishViewModel>()
            val generateImageState = sendWishViewModel.generateImageState.collectAsStateWithLifecycle().value
            val modelsList = sendWishViewModel.modelsList.collectAsStateWithLifecycle().value
            val sendWishState = sendWishViewModel.sendWishState.collectAsStateWithLifecycle().value
            val uploadImageState = sendWishViewModel.uploadImageState.collectAsStateWithLifecycle().value
            SendWishScreen(
                holidayDate = args.holidayDate,
                key = args.key,
                holidayName = args.key, currentDate = args.currentDate, generateImageState = generateImageState, onGenerateImage = { prompt, model ->
                sendWishViewModel.generateImage(prompt = prompt, model = model)
            }, modelsState = modelsList, sendWishState = sendWishState, onSendWish = { text, wishDate, openDate, image, maxViewers, isBlurred, cost ->
                sendWishViewModel.onEvent(SendWishEvent.OnSendWish(text, wishDate, openDate, image, maxViewers, isBlurred, cost))
            }, uploadImageState = uploadImageState, onUploadImage = { uri->
                sendWishViewModel.onEvent(SendWishEvent.OnUploadImage(uri))
            }, onNavigateToHomeScreen = {
                navController.navigate(HomePage)
            }, onBackSendState = {
                sendWishViewModel.onEvent(SendWishEvent.OnSendBackState)
            })
        }
    }
}