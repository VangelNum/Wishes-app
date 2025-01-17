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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
            val scope = rememberCoroutineScope()
            val loginViewModel: LoginViewModel = hiltViewModel()
            val loginState = loginViewModel.loginUiState.collectAsState().value
            val registrationViewModel: UploadAvatarViewModel = hiltViewModel()
            val registrationState =
                registrationViewModel.registrationState.collectAsStateWithLifecycle().value
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            var shouldShowAppBar by remember {
                mutableStateOf(false)
            }
            navBackStackEntry?.destination?.let { currentDestination ->
                shouldShowAppBar =
                    !currentDestination.hasRoute(LoginPage::class) && !currentDestination.hasRoute(
                        RegistrationPage::class
                    ) && !currentDestination.hasRoute(UploadAvatarPage::class)
            }

            WisherappTheme {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            if (loginState is UiState.Success) {
                                ProfileImage(image = loginState.data.avatarUrl)
                            } else if (registrationState is UiState.Success) {
                                ProfileImage(image = registrationState.data.avatarUrl)
                            }
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
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            if (shouldShowAppBar) {
                                CenterAlignedTopAppBar(
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                                    ),
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            scope.launch {
                                                drawerState.apply {
                                                    if (isClosed) open() else close()
                                                }
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Menu,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    title = {
                                        Image(
                                            painter = painterResource(R.drawable.logo),
                                            contentDescription = null,
                                            modifier = Modifier.height(40.dp)
                                        )
                                    }
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
                                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).safeContentPadding(),
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

@Composable
fun ProfileImage(modifier: Modifier = Modifier, image: String?) {
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
    }
}