package com.vangelnum.wisher.features.auth.register.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.vangelnum.wisher.features.auth.core.model.AuthResponse

@Composable
fun UploadAvatarScreen(
    modifier: Modifier = Modifier,
    registrationState: UiState<AuthResponse>,
    uploadAvatarState: UiState<String>,
    updateAvatarState: UiState<AuthResponse>,
    onNavigateToHome: () -> Unit,
    onEvent: (RegistrationEvent) -> Unit,
    onUpdateUserInfo: () -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                selectedImageUri = it
                onEvent(RegistrationEvent.OnUploadAvatar(context, it))
            }
        }
    )

    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Успешная регистрация",
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
                    val imageUrl = uploadAvatarState.data
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.defaultprofilephoto),
                        contentDescription = "Default profile photo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (selectedImageUri != null && uploadAvatarState is UiState.Success) {
                TextButton(onClick = {
                    selectedImageUri = null
                    onEvent(RegistrationEvent.OnBackToEmptyState)
                }) {
                    Text("Удалить фото")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
                enabled = updateAvatarState !is UiState.Loading
            ) {
                Text(if (selectedImageUri != null) "Выбрать другое фото из галереи" else "Выбрать из галереи")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "или",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedImageUri != null && uploadAvatarState is UiState.Success) {
                        onEvent(RegistrationEvent.OnUpdateAvatar(uploadAvatarState.data))
                        onNavigateToHome()
                    } else {
                        onNavigateToHome()
                    }
                    onUpdateUserInfo()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
                enabled = registrationState !is UiState.Loading
            ) {
                Text("Продолжить")
            }

            if (registrationState is UiState.Loading || uploadAvatarState is UiState.Loading || updateAvatarState is UiState.Loading) {
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

            if (updateAvatarState is UiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = updateAvatarState.message,
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
        UploadAvatarScreen(
            registrationState = UiState.Idle(),
            uploadAvatarState = UiState.Idle(),
            updateAvatarState = UiState.Idle(),
            onNavigateToHome = {},
            onEvent = {},
            onUpdateUserInfo = {}
        )
    }
}