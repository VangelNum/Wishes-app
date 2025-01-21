package com.vangelnum.wisher

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
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
import com.vangelnum.wisher.features.home.getwish.presentation.GetWishEvent
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
    registrationState: UiState<AuthResponse>,
    showSnackbar: (String) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<RegistrationPage>(
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            }
        ) {
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
        composable<LoginPage>(
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            }
        ) {
            LoginScreen(
                loginState = loginState,
                onBackToEmptyState = {
                    loginViewModel.onEvent(LoginEvent.OnBackToEmptyState)
                },
                onNavigateToHomeScreen = {
                    navController.navigate(HomePage) {
                        popUpTo(0)
                    }
                },
                onLoginUser = { email, password ->
                    loginViewModel.onEvent(LoginEvent.OnLoginUser(email, password))
                },
                onNavigateToRegisterScreen = {
                    navController.navigate(RegistrationPage)
                }
            )
        }
        composable<ProfilePage>(
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            }
        ) {
            Text("Profile page")
        }
        composable<HomePage>(
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            }
        ) {
            val wishKeyViewModel: WishKeyViewModel = hiltViewModel()
            val wishesViewModel: GetWishViewModel = hiltViewModel()
            val keyUiState = wishKeyViewModel.keyUiState.collectAsStateWithLifecycle().value
            val wishesDatesState = wishesViewModel.wishesDatesState.collectAsStateWithLifecycle().value
            val dateUiState = wishKeyViewModel.dateUiState.collectAsStateWithLifecycle().value
            val wishState = wishesViewModel.wishesState.collectAsStateWithLifecycle().value
            HomeScreen(
                keyUiState = keyUiState,
                currentDateUiState = dateUiState,
                onGetWishKey = {
                    wishKeyViewModel.onEvent(WishKeyEvent.OnGetWishKeyKey)
                },
                onGetTime = {
                    wishKeyViewModel.onEvent(WishKeyEvent.OnGetDate)
                },
                onNavigateHolidaysScreen = { holidayDate, key, currentDate ->
                    navController.navigate(HolidaysPage(holidayDate, key, currentDate))
                },
                wishesDatesState = wishesDatesState,
                onGetWishesDates = { key ->
                    wishesViewModel.onEvent(GetWishEvent.OnGetWishesDates(key))
                },
                showSnackbar = showSnackbar,
                wishState = wishState,
                onOpenWish = { key, id->
                    wishesViewModel.onEvent(GetWishEvent.OnGetWishes(key, id))
                },
                onRegenerateKey = {
                    wishKeyViewModel.onEvent(WishKeyEvent.OnRegenerateWishKey)
                }
            )
        }
        composable<UploadAvatarPage>(
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            }
        ) { back ->
            val args = back.toRoute<UploadAvatarPage>()
            val uploadAvatarState = registrationViewModel.uploadAvatarUiState.collectAsStateWithLifecycle().value
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
        composable<HolidaysPage>(
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            }
        ) { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<HolidaysPage>()
            val holidaysViewModel = hiltViewModel<HolidaysViewModel>()
            val holidaysState = holidaysViewModel.holidayUiState.collectAsStateWithLifecycle().value
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
                    navController.navigate(
                        SendWishPage(
                            holidayDate = holidayDate,
                            key = key,
                            holidayName = holiday.name,
                            currentDate = currentDate
                        )
                    )
                }
            )
        }
        composable<SendWishPage>(
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Left
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(300),
                    towards = AnimatedContentTransitionScope.SlideDirection.Right
                )
            }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<SendWishPage>()
            val sendWishViewModel = hiltViewModel<SendWishViewModel>()
            val sendWishUiState = sendWishViewModel.sendWishUiState.collectAsStateWithLifecycle().value
            val locale =  Locale.current
            val languageCode = Locale(locale.language).toLanguageTag()
            SendWishScreen(
                holidayDate = args.holidayDate,
                key = args.key,
                holidayName = args.holidayName,
                currentDate = args.currentDate,
                sendWishState = sendWishUiState,
                onGenerateImage = { prompt, model ->
                    sendWishViewModel.generateImageWithPrompt(prompt = prompt, model = model)
                },
                onSendWish = { text, wishDate, openDate, image, maxViewers, isBlurred, cost ->
                    sendWishViewModel.onEvent(
                        SendWishEvent.OnSendWish(
                            text,
                            wishDate,
                            openDate,
                            image,
                            maxViewers,
                            isBlurred,
                            cost
                        )
                    )
                },
                onUploadImage = { uri ->
                    sendWishViewModel.onEvent(SendWishEvent.OnUploadImage(uri))
                },
                onNavigateToHomeScreen = {
                    navController.navigate(HomePage)
                },
                onBackSendState = {
                    sendWishViewModel.onEvent(SendWishEvent.OnSendBackState)
                },
                onGenerateTextWishPrompt = { holidayName->
                    sendWishViewModel.onEvent(SendWishEvent.OnGenerateWishPromptByHoliday(holiday = holidayName, languageCode = languageCode))
                }, onImprovePrompt = { wishText ->
                    sendWishViewModel.onEvent(SendWishEvent.OnImproveWishPrompt(wishText, languageCode = languageCode))
                }
            )
        }
    }
}