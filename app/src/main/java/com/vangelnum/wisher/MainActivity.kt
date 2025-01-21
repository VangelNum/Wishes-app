package com.vangelnum.wisher

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.presentation.login.LoginEvent
import com.vangelnum.wisher.features.auth.presentation.login.LoginViewModel
import com.vangelnum.wisher.features.auth.presentation.registration.stage2.presentation.UploadAvatarViewModel
import com.vangelnum.wisher.ui.theme.WisherappTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
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
            val loginViewModel: LoginViewModel = hiltViewModel()
            val loginState = loginViewModel.loginUiState.collectAsState().value
            val registrationViewModel: UploadAvatarViewModel = hiltViewModel()
            val registrationState =
                registrationViewModel.registrationUiState.collectAsStateWithLifecycle().value
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
                    !currentDestination.hasRoute(LoginPage::class) &&
                            !currentDestination.hasRoute(RegistrationPage::class) &&
                            !currentDestination.hasRoute(UploadAvatarPage::class)
            }
            navBackStackEntry?.destination?.let { currentDestination ->
                showMenuIcon = currentDestination.hasRoute(HomePage::class)
            }
            val view = LocalView.current
            val statusBarHeight = with(LocalDensity.current) {
                WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                    .getInsets(WindowInsetsCompat.Type.statusBars())
                    .top.toDp()
            }
            val navigationBarHeight = with(LocalDensity.current) {
                WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                    .getInsets(WindowInsetsCompat.Type.navigationBars())
                    .bottom.toDp()
            }

            WisherappTheme {
                ModalNavigationDrawer(
                    gesturesEnabled = drawerState.isOpen,
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.padding(
                                top = statusBarHeight,
                                bottom = navigationBarHeight
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        16.dp,
                                        Alignment.CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (loginState is UiState.Success) {
                                        ProfileImage(
                                            image = loginState.data.avatarUrl,
                                            name = loginState.data.name
                                        )
                                    } else if (registrationState is UiState.Success) {
                                        ProfileImage(
                                            image = registrationState.data.avatarUrl,
                                            name = registrationState.data.name
                                        )
                                    }
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "close"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(
                                color = androidx.compose.ui.graphics.Color.Gray.copy(
                                    alpha = 0.7f
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            NavigationDrawerItem(
                                label = { Text(text = "Профиль") },
                                selected = false,
                                onClick = {

                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = "person"
                                    )
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text(text = "Отправленные пожелания") },
                                selected = false,
                                onClick = {

                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "wishes_send"
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            NavigationDrawerItem(
                                label = { Text(text = stringResource(R.string.exit)) },
                                selected = false,
                                onClick = {
                                    loginViewModel.onEvent(LoginEvent.OnExit)
                                    scope.launch {
                                        drawerState.close()
                                    }
                                    navController.navigate(LoginPage) {
                                        popUpTo(0)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "exit"
                                    )
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            if (shouldShowAppBar) {
                                AppTopBar(
                                    modifier = Modifier,
                                    loginState = loginState,
                                    registrationState = registrationState,
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
                                registrationState = registrationState,
                                showSnackbar = { message ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileImage(modifier: Modifier = Modifier, image: String?, name: String) {
    if (image == null) {
        Card(
            shape = CircleShape,
            modifier = modifier
                .padding(start = 8.dp)
                .size(75.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.defaultprofilephoto),
                contentDescription = "Avatar",
                modifier = Modifier.size(75.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(name, style = MaterialTheme.typography.titleLarge)
    } else {
        Card(
            shape = CircleShape,
            modifier = modifier
                .padding(start = 8.dp)
                .size(75.dp)
        ) {
            AsyncImage(
                model = image,
                contentDescription = "Avatar",
                modifier = Modifier.size(75.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(name, style = MaterialTheme.typography.titleLarge)
    }
}