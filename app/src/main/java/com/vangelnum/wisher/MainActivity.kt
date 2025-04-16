package com.vangelnum.wisher

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vangelnum.wisher.core.presentation.ObserveAsEvents
import com.vangelnum.wisher.core.presentation.SnackbarController
import com.vangelnum.wisher.features.auth.login.presentation.LoginEvent
import com.vangelnum.wisher.features.auth.login.presentation.LoginViewModel
import com.vangelnum.wisher.features.auth.register.presentation.RegisterUserViewModel
import com.vangelnum.wisher.ui.theme.WisherappTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            ObserveAsEvents(
                flow = SnackbarController.events,
                snackbarHostState
            ) { event ->
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()

                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action?.name,
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        event.action?.action?.invoke()
                    }
                }
            }
            val loginViewModel: LoginViewModel = hiltViewModel()
            val loginState = loginViewModel.loginUiState.collectAsStateWithLifecycle().value
            val registrationViewModel: RegisterUserViewModel = hiltViewModel()
            val registrationState = registrationViewModel.registrationUiState.collectAsStateWithLifecycle().value
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            var shouldShowAppBar by remember {
                mutableStateOf(false)
            }
            var showMenuIcon by remember {
                mutableStateOf(false)
            }
            navBackStackEntry?.destination?.let { currentDestination ->
                shouldShowAppBar =
                    !currentDestination.hasRoute(LoginPage::class) && !currentDestination.hasRoute(
                        RegistrationPage::class
                    ) && !currentDestination.hasRoute(UploadAvatarPage::class)
            }
            navBackStackEntry?.destination?.let { currentDestination ->
                showMenuIcon = currentDestination.hasRoute(HomePage::class)
            }

            WisherappTheme {
                ModalNavigationDrawer(
                    gesturesEnabled = drawerState.isOpen,
                    drawerState = drawerState,
                    drawerContent = {
                        AppDrawerSheet(
                            loginState,
                            onCloseDrawer = {
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            onNavigateSendingHistory = {
                                navController.navigate(UserWishesHistoryPage)
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            onNavigateToKeyLogsHistory = {
                                navController.navigate(KeyLogsHistoryPage)
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            onNavigateToWidgets = {
                                navController.navigate(WidgetPage)
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            onNavigateToShop = {
                                navController.navigate(BonusPage)
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            onExit = {
                                loginViewModel.onEvent(LoginEvent.OnExit)
                                scope.launch {
                                    drawerState.close()
                                }
                                navController.navigate(LoginPage) {
                                    popUpTo(0)
                                }
                            },
                            onNavigateProfileScreen = {
                                navController.navigate(ProfilePage)
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            if (shouldShowAppBar) {
                                AppTopBar(
                                    modifier = Modifier,
                                    loginState = loginState,
                                    onBack = {
                                        navController.popBackStack()
                                    },
                                    showMenuIcon = showMenuIcon,
                                    scope = scope,
                                    drawerState = drawerState
                                )
                            }
                        },
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    ) { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(R.drawable.background),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .blur(15.dp),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f
                            )
                            AppNavHost(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                loginViewModel = loginViewModel,
                                loginState = loginState,
                                registrationViewModel = registrationViewModel,
                                registrationState = registrationState
                            )
                        }
                    }
                }
            }
        }
    }
}