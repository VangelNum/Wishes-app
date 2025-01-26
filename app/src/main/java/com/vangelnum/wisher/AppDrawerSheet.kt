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
                onNavigateProfileScreen()
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
            onClick = onNavigateSendingHistory,
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