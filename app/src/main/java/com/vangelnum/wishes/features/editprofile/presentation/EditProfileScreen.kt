package com.vangelnum.wishes.features.editprofile.presentation

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.presentation.ErrorScreen
import com.vangelnum.wishes.core.presentation.LoadingScreen
import com.vangelnum.wishes.core.presentation.SmallLoadingIndicator
import com.vangelnum.wishes.features.auth.core.model.AuthResponse

@Composable
fun EditProfileScreen(
    userAvatarUrl: String,
    userName: String,
    userEmail: String,
    onEditUserInfo: (
        userName: String,
        userEmail: String,
        avatar: String,
        newPassword: String,
        currentPassword: String
    ) -> Unit,
    editProfileState: UiState<AuthResponse>,
    onBackToEmptyState: () -> Unit,
    onNavigateToHome: () -> Unit,
    onUploadAvatar: (imageUri: Uri, context: Context) -> Unit,
    uploadAvatarState: UiState<String>
) {
    when (editProfileState) {
        is UiState.Error -> {
            ErrorScreen(
                editProfileState.message,
                buttonMessage = stringResource(R.string.back_button),
                onButtonClick = { onBackToEmptyState() }
            )
        }

        is UiState.Idle -> {
            EditProfileContent(
                userAvatarUrl = userAvatarUrl,
                userName = userName,
                userEmail = userEmail,
                onEditUserInfo = onEditUserInfo,
                onUploadAvatar = onUploadAvatar,
                uploadAvatarState = uploadAvatarState
            )
        }

        is UiState.Loading -> {
            LoadingScreen(stringResource(R.string.update_profile_waiting))
        }

        is UiState.Success -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.happystate),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                    Text(
                        stringResource(R.string.profile_updated_successfully),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Button(
                        onClick = onNavigateToHome,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text(stringResource(R.string.back_to_home))
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileContent(
    userAvatarUrl: String,
    userName: String,
    userEmail: String,
    onEditUserInfo: (
        userName: String,
        userEmail: String,
        avatar: String,
        newPassword: String,
        currentPassword: String
    ) -> Unit,
    onUploadAvatar: (imageUri: Uri, context: Context) -> Unit,
    uploadAvatarState: UiState<String>
) {
    val context = LocalContext.current

    var editableUserName by rememberSaveable { mutableStateOf(userName) }
    var editableEmail by rememberSaveable { mutableStateOf(userEmail) }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var currentPassword by rememberSaveable { mutableStateOf("") }

    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var newAvatarUrl by rememberSaveable { mutableStateOf<String?>(null) }
    val isAvatarUploading = uploadAvatarState is UiState.Loading

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                onUploadAvatar(uri, context)
            }
        }
    )

    LaunchedEffect(uploadAvatarState) {
        if (uploadAvatarState is UiState.Success) {
            newAvatarUrl = uploadAvatarState.data
        }
    }

    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterNewPassword = remember { FocusRequester() }
    val focusRequesterCurrentPassword = remember { FocusRequester() }

    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isCurrentPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            stringResource(R.string.profile_editing),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Box(contentAlignment = Alignment.Center) {
            Box(contentAlignment = Alignment.BottomEnd) {
                OutlinedCard(
                    shape = CircleShape,
                    elevation = CardDefaults.outlinedCardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clickable(enabled = !isAvatarUploading) {
                            imagePickerLauncher.launch("image/*")
                        }
                ) {
                    SubcomposeAsyncImage(
                        model = selectedImageUri ?: newAvatarUrl,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        contentDescription = stringResource(R.string.user_avatar),
                        loading = {
                            SmallLoadingIndicator(modifier = Modifier.fillMaxSize())
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_avatar),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            if (isAvatarUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(120.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            thickness = 1.dp
        )
        InputTextField(
            labelResId = R.string.username,
            placeholderResId = R.string.enter_your_name,
            value = editableUserName,
            onValueChange = { editableUserName = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterEmail.requestFocus() }),
            focusRequester = focusRequesterName,
        )

        InputTextField(
            labelResId = R.string.email,
            placeholderResId = R.string.enter_your_email,
            value = editableEmail,
            onValueChange = { editableEmail = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterNewPassword.requestFocus() }),
            focusRequester = focusRequesterEmail,
        )

        InputTextField(
            labelResId = R.string.new_password,
            placeholderResId = R.string.enter_new_password_optional,
            value = newPassword,
            onValueChange = { newPassword = it.trim() },
            visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterCurrentPassword.requestFocus() }),
            focusRequester = focusRequesterNewPassword,
            trailingIcon = {
                val image = if (isNewPasswordVisible) R.drawable.visibility else R.drawable.visibility_off
                IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                    Icon(
                        painter = painterResource(image),
                        contentDescription = stringResource(R.string.toggle_password_visibility),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
        )
        InputTextField(
            labelResId = R.string.current_password,
            placeholderResId = R.string.enter_current_password_to_save,
            value = currentPassword,
            onValueChange = {
                currentPassword = it.trim()
            },
            visualTransformation = if (isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Действие по кнопке Done */ }),
            focusRequester = focusRequesterCurrentPassword,
            trailingIcon = {
                val image = if (isCurrentPasswordVisible) R.drawable.visibility else R.drawable.visibility_off
                IconButton(onClick = { isCurrentPasswordVisible = !isCurrentPasswordVisible }) {
                    Icon(
                        painter = painterResource(image),
                        contentDescription = stringResource(R.string.toggle_password_visibility),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val avatarUrlToSave = newAvatarUrl ?: userAvatarUrl
                onEditUserInfo(
                    editableUserName.trim(),
                    editableEmail.trim(),
                    avatarUrlToSave,
                    newPassword.trim(),
                    currentPassword.trim()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            shape = RoundedCornerShape(54.dp),
            enabled = !isAvatarUploading && currentPassword.isNotBlank()
        ) {
            Text(
                stringResource(R.string.save_changes),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}


@Composable
fun InputTextField(
    @StringRes labelResId: Int,
    @StringRes placeholderResId: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    focusRequester: FocusRequester,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(labelResId),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    stringResource(placeholderResId),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            isError = isError,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}