package com.vangelnum.wishes.features.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.presentation.ErrorScreen
import com.vangelnum.wishes.core.presentation.LoadingScreen
import com.vangelnum.wishes.features.auth.core.model.AuthResponse


@Composable
fun ProfileScreen(
    userInfoState: UiState<AuthResponse>,
    onNavigateEditProfilePage: () -> Unit
) {
    when (userInfoState) {
        is UiState.Error -> ErrorScreen(userInfoState.message)
        is UiState.Loading -> LoadingScreen(stringResource(R.string.loading_profile))
        is UiState.Success -> {
            ProfileContent(
                data = userInfoState.data,
                onNavigateEditProfilePage = onNavigateEditProfilePage
            )
        }

        is UiState.Idle -> {}
    }
}

@Composable
fun ProfileContent(
    data: AuthResponse,
    onNavigateEditProfilePage: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.size(100.dp),
            shape = CircleShape
        ) {
            AsyncImage(
                model = data.avatarUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(text = data.name, style = MaterialTheme.typography.headlineMedium)
        Text(text = data.email)
        ElevatedButton(onClick = {
            onNavigateEditProfilePage()
        }) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.edit_profile))
        }
    }
}