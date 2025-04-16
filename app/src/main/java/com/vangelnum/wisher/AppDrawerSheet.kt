package com.vangelnum.wisher

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import coil.compose.AsyncImage
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.auth.core.model.AuthResponse

@Composable
fun AppDrawerSheet(
    loginState: UiState<AuthResponse>,
    onCloseDrawer: () -> Unit,
    onNavigateSendingHistory: () -> Unit,
    onNavigateProfileScreen: () -> Unit,
    onNavigateToKeyLogsHistory: () -> Unit,
    onNavigateToWidgets: () -> Unit,
    onNavigateToShop: () -> Unit,
    onExit: () -> Unit
) {
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
                }
            }
            IconButton(onClick = onCloseDrawer) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close_drawer)
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
            label = { Text(text = stringResource(R.string.profile)) },
            selected = false,
            onClick = {
                onNavigateProfileScreen()
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = stringResource(R.string.profile)
                )
            }
        )
        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.sent_wishes)) },
            selected = false,
            onClick = onNavigateSendingHistory,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.sent_wishes)
                )
            }
        )
        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.viewed_keys)) },
            selected = false,
            onClick = {
                onNavigateToKeyLogsHistory()
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_vpn_key_24),
                    contentDescription = stringResource(R.string.viewed_keys_icon)
                )
            }
        )
        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.widgets)) },
            selected = false,
            onClick = {
                onNavigateToWidgets()
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_widgets_24),
                    contentDescription = stringResource(R.string.widgets_icon)
                )
            }
        )

        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.bonus)) },
            selected = false,
            onClick = onNavigateToShop,
            icon = {
                Icon(
                    painterResource(R.drawable.round_monetization_on_24),
                    contentDescription = stringResource(R.string.bonus)
                )
            }
        )

        Spacer(modifier = Modifier.weight(1f))
        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.exit)) },
            selected = false,
            onClick = onExit,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = stringResource(R.string.exit)
                )
            }
        )
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
                contentDescription = stringResource(R.string.default_avatar),
                modifier = Modifier.size(75.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(text = name, style = MaterialTheme.typography.titleLarge)
    } else {
        Card(
            shape = CircleShape,
            modifier = modifier
                .padding(start = 8.dp)
                .size(75.dp)
        ) {
            AsyncImage(
                model = image,
                contentDescription = stringResource(R.string.user_avatar),
                modifier = Modifier.size(75.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(text = name, style = MaterialTheme.typography.titleLarge)
    }
}