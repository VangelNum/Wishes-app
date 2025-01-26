package com.vangelnum.wisher

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.intl.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.auth.login.presentation.LoginEvent
import com.vangelnum.wisher.features.auth.login.presentation.LoginScreen
import com.vangelnum.wisher.features.auth.login.presentation.LoginViewModel
import com.vangelnum.wisher.features.auth.register.data.model.RegistrationRequest
import com.vangelnum.wisher.features.auth.register.presentation.RegisterUserViewModel
import com.vangelnum.wisher.features.auth.register.presentation.RegistrationEvent
import com.vangelnum.wisher.features.auth.register.presentation.RegistrationScreen
import com.vangelnum.wisher.features.auth.register.presentation.UploadAvatarScreen
import com.vangelnum.wisher.features.auth.register.presentation.email_verification.OtpAction
import com.vangelnum.wisher.features.auth.register.presentation.email_verification.OtpViewModel
import com.vangelnum.wisher.features.auth.register.presentation.email_verification.VerifyEmailScreen
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
import com.vangelnum.wisher.features.profile.presentation.ProfileScreen
import com.vangelnum.wisher.features.profile.presentation.UpdateProfileViewModel
import com.vangelnum.wisher.features.userviewhistory.presentation.ViewHistoryEvent
import com.vangelnum.wisher.features.userviewhistory.presentation.ViewHistoryScreen
import com.vangelnum.wisher.features.userviewhistory.presentation.ViewHistoryViewModel
import com.vangelnum.wisher.features.userwisheshistory.presentation.UserWishesHistoryScreen
import com.vangelnum.wisher.features.userwisheshistory.presentation.UserWishesHistoryViewModel

data class ComposableAnimationSpecs(
    val enter: EnterTransition,
    val exit: ExitTransition,
    val popEnter: EnterTransition,
    val popExit: ExitTransition
)

fun AnimatedContentTransitionScope<*>.defaultComposableAnimation(): ComposableAnimationSpecs {
    val enterTransition = slideIntoContainer(
        animationSpec = tween(300),
        towards = AnimatedContentTransitionScope.SlideDirection.Left
    )
    val exitTransition = slideOutOfContainer(
        animationSpec = tween(300),
        towards = AnimatedContentTransitionScope.SlideDirection.Left
    )
    val popEnterTransition = slideIntoContainer(
        animationSpec = tween(300),
        towards = AnimatedContentTransitionScope.SlideDirection.Right
    )
    val popExitTransition = slideOutOfContainer(
        animationSpec = tween(300),
        towards = AnimatedContentTransitionScope.SlideDirection.Right
    )

    return ComposableAnimationSpecs(
        enter = enterTransition,
        exit = exitTransition,
        popEnter = popEnterTransition,
        popExit = popExitTransition
    )
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any = LoginPage,
    loginViewModel: LoginViewModel,
    loginState: UiState<AuthResponse>,
    registrationViewModel: RegisterUserViewModel,
    registrationState: UiState<AuthResponse>,
    showSnackbar: (String) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<RegistrationPage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) {
            val pendingRegistrationState by registrationViewModel.pendingRegistrationUiState.collectAsStateWithLifecycle()
            RegistrationScreen(
                onNavigateToLoginPage = {
                    navController.navigate(LoginPage)
                },
                onNavigateToVerifyEmail = { email, password ->
                    navController.navigate(VerifyEmailPage(email, password))
                },
                onRegisterUser = { name, email, password ->
                    registrationViewModel.onEvent(
                        RegistrationEvent.OnRegisterUser(
                            RegistrationRequest(name, email, password)
                        )
                    )
                },
                pendingRegistrationState = pendingRegistrationState,
                onBackRegistrationState = {
                    registrationViewModel.onEvent(RegistrationEvent.OnBackToEmptyState)
                }
            )
        }
        composable<LoginPage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
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
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) {
            val updatedProfileViewModel = hiltViewModel<UpdateProfileViewModel>()
            if (loginState is UiState.Success) {
                ProfileScreen(userInfoState = loginViewModel.loginUiState.value, onUpdateProfile = { name, email, password, imageUri, context ->
                    updatedProfileViewModel.updateProfile(name, email, password, imageUri, context)
                })
            }
        }
        composable<HomePage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) {
            val wishKeyViewModel: WishKeyViewModel = hiltViewModel()
            val wishesViewModel: GetWishViewModel = hiltViewModel()
            val keyUiState = wishKeyViewModel.keyUiState.collectAsStateWithLifecycle().value
            val wishesDatesState =
                wishesViewModel.wishesDatesState.collectAsStateWithLifecycle().value
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
                showSnackbar = showSnackbar,
                wishState = wishState,
                onRegenerateKey = {
                    wishKeyViewModel.onEvent(WishKeyEvent.OnRegenerateWishKey)
                },
                onEvent = { event ->
                    wishesViewModel.onEvent(event)
                }
            )
        }
        composable<UploadAvatarPage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) { backStack->
            val args = backStack.toRoute<UploadAvatarPage>()
            val uploadAvatarState = registrationViewModel.uploadAvatarUiState.collectAsStateWithLifecycle().value
            val updateAvatarUiState = registrationViewModel.updateAvatarUiState.collectAsStateWithLifecycle().value
            UploadAvatarScreen(
                registrationState = registrationState,
                uploadAvatarState = uploadAvatarState,
                onEvent = { event ->
                    registrationViewModel.onEvent(event)
                },
                onNavigateToHome = {
                    loginViewModel.onEvent(LoginEvent.OnLoginUser(args.email, args.password))
                    navController.navigate(HomePage) {
                        popUpTo(0)
                    }
                },
                updateAvatarState = updateAvatarUiState,
                onUpdateUserInfo = {
                    loginViewModel.onEvent(LoginEvent.OnLoginUser(args.email, args.password))
                }
            )
        }
        composable<HolidaysPage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
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
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<SendWishPage>()
            val sendWishViewModel = hiltViewModel<SendWishViewModel>()
            val sendWishUiState = sendWishViewModel.sendWishUiState.collectAsStateWithLifecycle().value
            val locale = Locale.current
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
                    navController.navigate(HomePage) {
                        popUpTo(0)
                    }
                },
                onBackSendState = {
                    sendWishViewModel.onEvent(SendWishEvent.OnSendBackState)
                },
                onGenerateTextWishPrompt = { holidayName ->
                    sendWishViewModel.onEvent(
                        SendWishEvent.OnGenerateWishPromptByHoliday(
                            holiday = holidayName,
                            languageCode = languageCode
                        )
                    )
                }, onImprovePrompt = { wishText ->
                    sendWishViewModel.onEvent(
                        SendWishEvent.OnImproveWishPrompt(
                            wishText,
                            languageCode = languageCode
                        )
                    )
                }
            )
        }
        composable<VerifyEmailPage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) { backStack ->
            val args = backStack.toRoute<VerifyEmailPage>()

            val otpViewModel = hiltViewModel<OtpViewModel>()
            val otpState by otpViewModel.state.collectAsStateWithLifecycle()
            val focusRequesters = remember {
                List(6) { FocusRequester() }
            }
            val focusManager = LocalFocusManager.current
            val keyboardManager = LocalSoftwareKeyboardController.current

            LaunchedEffect(otpState.focusedIndex) {
                otpState.focusedIndex?.let { index ->
                    focusRequesters.getOrNull(index)?.requestFocus()
                }
            }

            LaunchedEffect(otpState.code, keyboardManager) {
                val allNumbersEntered = otpState.code.none { it == null }
                if (allNumbersEntered) {
                    focusRequesters.forEach {
                        it.freeFocus()
                    }
                    focusManager.clearFocus()
                    keyboardManager?.hide()
                    val verificationCode = otpState.code.joinToString("")
                    registrationViewModel.onEvent(
                        RegistrationEvent.OnVerifyEmail(
                            args.email,
                            verificationCode
                        )
                    )
                }
            }

            VerifyEmailScreen(
                email = args.email,
                otpState = otpState,
                focusRequesters = focusRequesters,
                onAction = { action ->
                    when (action) {
                        is OtpAction.OnEnterNumber -> {
                            if (action.number != null) {
                                focusRequesters[action.index].freeFocus()
                            }
                        }

                        else -> Unit
                    }
                    otpViewModel.onAction(action)
                },
                registrationState = registrationState,
                onNavigateUploadAvatarScreen = {
                    navController.navigate(UploadAvatarPage(args.email, args.password))
                },
                modifier = Modifier
            )
        }
        composable<UserWishesHistoryPage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) {
            val userWishesHistoryViewModel = hiltViewModel<UserWishesHistoryViewModel>()
            val sendingHistoryWishes = userWishesHistoryViewModel.mySendingWishesState.collectAsStateWithLifecycle().value
            UserWishesHistoryScreen(sendingHistoryWishes, onNavigateToViewHistoryScreen = { wishId->
                navController.navigate(ViewHistoryPage(wishId))
            }, modifier = Modifier.fillMaxSize())
        }
        composable<ViewHistoryPage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) { backStack ->
            val args = backStack.toRoute<ViewHistoryPage>()
            val wishId = args.wishId
            val viewHistoryViewModel = hiltViewModel<ViewHistoryViewModel>()
            val viewHistoryState = viewHistoryViewModel.viewHistoryState.collectAsStateWithLifecycle().value
            ViewHistoryScreen(onLoadViewHistory = {
                viewHistoryViewModel.onEvent(ViewHistoryEvent.OnGetViewHistory(wishId))
            }, state = viewHistoryState)
        }
    }
}