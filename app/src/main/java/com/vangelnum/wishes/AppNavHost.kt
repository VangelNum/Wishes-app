package com.vangelnum.wishes

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.presentation.ErrorScreen
import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import com.vangelnum.wishes.features.auth.login.presentation.LoginEvent
import com.vangelnum.wishes.features.auth.login.presentation.LoginScreen
import com.vangelnum.wishes.features.auth.login.presentation.LoginViewModel
import com.vangelnum.wishes.features.auth.register.presentation.RegisterUserViewModel
import com.vangelnum.wishes.features.auth.register.presentation.RegistrationEvent
import com.vangelnum.wishes.features.auth.register.presentation.RegistrationScreen
import com.vangelnum.wishes.features.auth.register.presentation.UploadAvatarScreen
import com.vangelnum.wishes.features.auth.register.presentation.email_verification.OtpAction
import com.vangelnum.wishes.features.auth.register.presentation.email_verification.OtpViewModel
import com.vangelnum.wishes.features.auth.register.presentation.email_verification.VerifyEmailScreen
import com.vangelnum.wishes.features.bonus.presentation.BonusEvent
import com.vangelnum.wishes.features.bonus.presentation.BonusScreen
import com.vangelnum.wishes.features.bonus.presentation.BonusViewModel
import com.vangelnum.wishes.features.buns.presentation.BunsScreen
import com.vangelnum.wishes.features.editprofile.presentation.EditProfileScreen
import com.vangelnum.wishes.features.editprofile.presentation.EditProfileViewModel
import com.vangelnum.wishes.features.home.HomeScreen
import com.vangelnum.wishes.features.home.getwish.presentation.GetWishViewModel
import com.vangelnum.wishes.features.home.sendwish.createwish.presentation.SendWishEvent
import com.vangelnum.wishes.features.home.sendwish.createwish.presentation.SendWishScreen
import com.vangelnum.wishes.features.home.sendwish.createwish.presentation.SendWishViewModel
import com.vangelnum.wishes.features.home.sendwish.selectdate.wishkey.presentation.WishKeyEvent
import com.vangelnum.wishes.features.home.sendwish.selectdate.wishkey.presentation.WishKeyViewModel
import com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.presentation.WorldTimeViewModel
import com.vangelnum.wishes.features.home.sendwish.selectholiday.presentation.HolidaysScreen
import com.vangelnum.wishes.features.home.sendwish.selectholiday.presentation.HolidaysViewModel
import com.vangelnum.wishes.features.keylogshistory.presentation.KeyLogsHistoryScreen
import com.vangelnum.wishes.features.keylogshistory.presentation.KeyLogsHistoryViewModel
import com.vangelnum.wishes.features.profile.presentation.ProfileScreen
import com.vangelnum.wishes.features.userwishsendinghistory.presentation.UserWishesHistoryScreen
import com.vangelnum.wishes.features.userwishsendinghistory.presentation.UserWishesHistoryViewModel
import com.vangelnum.wishes.features.userwishviewhistory.presentation.ViewHistoryEvent
import com.vangelnum.wishes.features.userwishviewhistory.presentation.ViewHistoryScreen
import com.vangelnum.wishes.features.userwishviewhistory.presentation.ViewHistoryViewModel
import com.vangelnum.wishes.features.widget.WidgetScreen

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
    wishKeyFromWidget: String?
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
                pendingRegistrationState = pendingRegistrationState,
                onEvent = { event ->
                    registrationViewModel.onEvent(event)
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
                onNavigateToHomeScreen = {
                    if (wishKeyFromWidget.isNullOrBlank()) {
                        navController.navigate(HomePage()) {
                            popUpTo(0)
                        }
                    } else {
                        navController.navigate(HomePage(key = wishKeyFromWidget, selectedTab = 1)) {
                            popUpTo(0)
                        }
                    }
                },
                onNavigateToRegisterScreen = {
                    navController.navigate(RegistrationPage)
                },
                onEvent = { event ->
                    loginViewModel.onEvent(event)
                }
            )
        }
        composable<ProfilePage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) {
            if (loginState is UiState.Success) {
                ProfileScreen(
                    userInfoState = loginViewModel.loginUiState.value,
                    onNavigateEditProfilePage = {
                        navController.navigate(EditProfilePage(
                            avatar = loginState.data.avatarUrl,
                            name = loginState.data.name,
                            email = loginState.data.email
                        ))
                    }
                )
            } else {
                ErrorScreen(stringResource(R.string.unknown_error))
            }
        }
        composable<EditProfilePage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) { backStack->
            val args = backStack.toRoute<EditProfilePage>()
            val editProfileViewModel = hiltViewModel<EditProfileViewModel>()
            val editProfileState = editProfileViewModel.editProfileState.collectAsStateWithLifecycle().value
            val uploadAvatarState = editProfileViewModel.uploadAvatarState.collectAsStateWithLifecycle().value
            EditProfileScreen(
                userAvatarUrl = args.avatar,
                userName = args.name,
                userEmail = args.email,
                onEditUserInfo = { userName, userEmail, avatar, newPassword, currentPassword ->
                    editProfileViewModel.editProfile(
                        name = userName,
                        email = userEmail,
                        avatarUrl = avatar,
                        newPassword = newPassword,
                        currentPassword = currentPassword
                    )
                },
                editProfileState = editProfileState,
                onBackToEmptyState = {
                    editProfileViewModel.backToEmptyState()
                },
                onNavigateToHome = {
                    navController.navigate(HomePage())
                },
                onUploadAvatar = { imageUri, context ->
                    editProfileViewModel.uploadAvatar(imageUri, context)
                },
                uploadAvatarState = uploadAvatarState
            )
        }
        composable<HomePage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<HomePage>()
            val wishKeyViewModel: WishKeyViewModel = hiltViewModel()
            val keyUiState = wishKeyViewModel.keyUiState.collectAsStateWithLifecycle().value

            val wishesViewModel: GetWishViewModel = hiltViewModel()
            val wishState = wishesViewModel.wishesState.collectAsStateWithLifecycle().value
            val wishesDatesState = wishesViewModel.wishesDatesState.collectAsStateWithLifecycle().value

            val worldTimeViewModel: WorldTimeViewModel = hiltViewModel()
            val currentDateUiState = worldTimeViewModel.currentDateUiState.collectAsStateWithLifecycle().value

            HomeScreen(
                keyUiState = keyUiState,
                currentDateUiState = currentDateUiState,
                onNavigateHolidaysScreen = { holidayDate, key, currentDate ->
                    navController.navigate(HolidaysPage(holidayDate, key, currentDate))
                },
                wishesDatesState = wishesDatesState,
                wishState = wishState,
                onRegenerateKey = {
                    wishKeyViewModel.onEvent(WishKeyEvent.OnRegenerateWishKey)
                },
                onGetWishEvent = { event ->
                    wishesViewModel.onEvent(event)
                },
                keyFromHistory = args.key,
                selectedTab = args.selectedTab,
                wishKeyFromWidget = wishKeyFromWidget
            )
        }
        composable<UploadAvatarPage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) { backStack ->
            val args = backStack.toRoute<UploadAvatarPage>()
            val uploadAvatarState = registrationViewModel.uploadAvatarUiState.collectAsStateWithLifecycle().value
            val updateAvatarUiState = registrationViewModel.updateAvatarUiState.collectAsStateWithLifecycle().value
            UploadAvatarScreen(
                registrationState = registrationState,
                uploadAvatarState = uploadAvatarState,
                onEvent = { event ->
                    registrationViewModel.onEvent(event)
                },
                onNavigateToBunsScreen = {
                    loginViewModel.onEvent(LoginEvent.OnLoginUser(args.email, args.password))
                    navController.navigate(BunsPage) {
                        popUpTo(0)
                    }
                },
                updateAvatarState = updateAvatarUiState,
                onUpdateUserInfo = {
                    loginViewModel.onEvent(LoginEvent.OnLoginUser(args.email, args.password))
                    loginViewModel.onEvent(LoginEvent.OnRefreshUser)
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
            HolidaysScreen(
                holidaysState = holidaysState,
                onTryAgainLoadingHolidays = {
                    holidaysViewModel.getHolidays(args.holidayDate)
                },
                onContinueClick = { holiday ->
                    navController.navigate(
                        SendWishPage(
                            holidayDate = args.holidayDate,
                            key = args.key,
                            holidayName = holiday.name,
                            currentDate = args.currentDate
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

            LaunchedEffect(args.holidayName) {
                if (args.holidayName.isNotEmpty()) {
                    sendWishViewModel.onEvent(SendWishEvent.OnGenerateWishPromptByHoliday(args.holidayName, languageCode = languageCode))
                }
            }

            SendWishScreen(
                holidayDate = args.holidayDate,
                key = args.key,
                currentDate = args.currentDate,
                languageCode = languageCode,
                sendWishUiState = sendWishUiState,
                onNavigateToHomeScreen = {
                    navController.navigate(HomePage()) {
                        popUpTo(0)
                    }
                },
                onEvent = { event ->
                    sendWishViewModel.onEvent(event)
                },
                refreshUserInfo = {
                    loginViewModel.onEvent(LoginEvent.OnRefreshUser)
                },
                userCoins = if (loginState is UiState.Success) loginState.data.coins else 0
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
            val resendVerificationCodeUiState = registrationViewModel.resendVerificationCodeUiState.collectAsStateWithLifecycle().value
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
                resendVerificationCodeUiState = resendVerificationCodeUiState,
                resendVerificationCode = {
                    registrationViewModel.onEvent(RegistrationEvent.OnResendVerificationCode(args.email))
                }
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
            val wishKeyViewModel: WishKeyViewModel = hiltViewModel()
            val keyUiState = wishKeyViewModel.keyUiState.collectAsStateWithLifecycle().value
            UserWishesHistoryScreen(
                sendingHistoryWishes,
                onNavigateToViewHistoryScreen = { wishId ->
                    navController.navigate(ViewHistoryPage(wishId))
                },
                modifier = Modifier.fillMaxSize(),
                onEvent = { event ->
                    userWishesHistoryViewModel.onEvent(event)
                },
                key = if (keyUiState is UiState.Success) keyUiState.data.key else "Error loading key"
            )
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
            val viewHistoryState =
                viewHistoryViewModel.viewHistoryState.collectAsStateWithLifecycle().value
            ViewHistoryScreen(onLoadViewHistory = {
                viewHistoryViewModel.onEvent(ViewHistoryEvent.OnGetViewHistory(wishId))
            }, state = viewHistoryState)
        }
        composable<KeyLogsHistoryPage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) {
            val keyLogsHistoryViewModel = hiltViewModel<KeyLogsHistoryViewModel>()
            val keyLogsHistoryState =
                keyLogsHistoryViewModel.keyLogsHistoryState.collectAsStateWithLifecycle().value
            KeyLogsHistoryScreen(keyLogsHistoryState, onSearchByKey = { key ->
                navController.navigate(HomePage(key, 1))
            }, Modifier.fillMaxSize())
        }
        composable<WidgetPage>(
            enterTransition = { this.defaultComposableAnimation().enter },
            exitTransition = { this.defaultComposableAnimation().exit },
            popEnterTransition = { this.defaultComposableAnimation().popEnter },
            popExitTransition = { this.defaultComposableAnimation().popExit }
        ) {
            WidgetScreen()
        }
        composable<BunsPage> {
            BunsScreen {
                navController.navigate(HomePage()) {
                    popUpTo(0)
                }
            }
        }
        composable<BonusPage> {
            val bonusViewModel = hiltViewModel<BonusViewModel>()
            val bonusUiState = bonusViewModel.bonusUiState.collectAsStateWithLifecycle().value
            val claimBonusUiState = bonusViewModel.claimBonusUiState.collectAsStateWithLifecycle().value
            val adRewardCooldownUiState = bonusViewModel.adRewardCooldownUiState.collectAsStateWithLifecycle().value
            val claimAdRewardUiState = bonusViewModel.claimAdRewardUiState.collectAsStateWithLifecycle().value
            BonusScreen(
                bonusUiState = bonusUiState,
                claimBonusUiState = claimBonusUiState,
                adRewardCooldownUiState = adRewardCooldownUiState,
                claimAdRewardUiState = claimAdRewardUiState,
                onClaimBonus = {
                    bonusViewModel.onEvent(BonusEvent.OnClaimBonus)
                },
                onClaimAdReward = {
                    bonusViewModel.onEvent(BonusEvent.OnClaimAdReward)
                }
            )

            LaunchedEffect(claimBonusUiState) {
                if (claimBonusUiState is UiState.Success) {
                    bonusViewModel.onEvent(BonusEvent.OnBackToEmptyState)
                    loginViewModel.onEvent(LoginEvent.OnRefreshUser)
                    bonusViewModel.onEvent(BonusEvent.OnGetBonusInfo)
                }
            }

            LaunchedEffect(claimAdRewardUiState) {
                if (claimAdRewardUiState is UiState.Success) {
                    bonusViewModel.onEvent(BonusEvent.OnGetAdRewardCooldownInfo)
                    loginViewModel.onEvent(LoginEvent.OnRefreshUser)
                }
            }
        }
    }
}