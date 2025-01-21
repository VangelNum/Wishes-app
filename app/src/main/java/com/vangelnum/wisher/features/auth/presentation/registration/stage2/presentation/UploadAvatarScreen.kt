package com.vangelnum.wisher.features.auth.presentation.registration.stage2.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.features.auth.data.model.AuthResponse
import com.vangelnum.wisher.features.auth.data.model.RegistrationRequest
import com.vangelnum.wisher.features.auth.presentation.registration.stage1.RegistrationEvent

@Composable
fun LoadAvatarScreen(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    password: String,
    registrationState: UiState<AuthResponse>,
    uploadAvatarState: UiState<String>,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onEvent: (RegistrationEvent) -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isDefaultAvatarSelected by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
            isDefaultAvatarSelected = false
            onEvent(RegistrationEvent.OnUploadAvatar(context, selectedImageUri!!))
        }
    )

    LaunchedEffect(key1 = registrationState) {
        if (registrationState is UiState.Success) {
            onNavigateToHome()
        }
    }

    Box {
        IconButton(onClick = {
            onNavigateBack()
        }, modifier = Modifier.padding(12.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Почти всё готово",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Давайте добавим фото",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = CircleShape,
                modifier = Modifier.size(150.dp)
            ) {
                if (uploadAvatarState is UiState.Success) {
                    val imageUrl =  uploadAvatarState.data
                    imageUrl.let {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier.size(150.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(R.drawable.defaultprofilephoto),
                        contentDescription = "Default profile photo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Выбрать из галереи")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "или",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val user = RegistrationRequest(name, email, password)
                if (selectedImageUri != null) {
                    if (uploadAvatarState is UiState.Success) {
                        onEvent(RegistrationEvent.OnRegisterUser(user.copy(avatarUrl = uploadAvatarState.data)))
                    }
                } else {
                    onEvent(RegistrationEvent.OnRegisterUser(user))
                }
            }, modifier = Modifier.fillMaxWidth(),
                enabled = uploadAvatarState !is UiState.Loading && registrationState !is UiState.Loading) {
                Text("Продолжить")
            }

            if (registrationState is UiState.Loading || uploadAvatarState is UiState.Loading) {
                Spacer(modifier = Modifier.height(8.dp))
                LoadingScreen()
            }

            if (registrationState is UiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = registrationState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (uploadAvatarState is UiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uploadAvatarState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadAvatarScreenPreview() {
    MaterialTheme {
        LoadAvatarScreen(
            name = "testuser",
            email = "test@example.com",
            registrationState = UiState.Idle(),
            password = "password",
            onNavigateBack = {},
            uploadAvatarState = UiState.Idle(),
            onNavigateToHome = {},
            onEvent = {}
        )
    }
}