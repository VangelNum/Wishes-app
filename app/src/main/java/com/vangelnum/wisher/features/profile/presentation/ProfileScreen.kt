package com.vangelnum.wisher.features.profile.presentation

import android.content.Context
import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.core.presentation.SnackbarController
import com.vangelnum.wisher.core.presentation.SnackbarEvent
import com.vangelnum.wisher.core.utils.string
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.auth.register.presentation.VerticalSpacer

@Composable
fun ProfileScreen(
    userInfoState: UiState<AuthResponse>,
    onUpdateProfile: (String?, String?, String?, String, String?, Context) -> Unit,
    updateProfileState: UiState<AuthResponse>,
    onUploadImage: (imageUri: Uri, context: Context) -> Unit,
    uploadImageState: UiState<String>,
    onUpdateUserInfo: (email: String, password: String) -> Unit,
    backToEmptyState: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (userInfoState) {
        is UiState.Error -> ErrorScreen(userInfoState.message)
        is UiState.Loading -> LoadingScreen("Loading profile info")
        is UiState.Success -> ProfileContent(
            data = userInfoState.data,
            onUpdateProfile = onUpdateProfile,
            updatedProfileState = updateProfileState,
            onUploadImage = onUploadImage,
            uploadImageState = uploadImageState,
            onUpdateUserInfo = onUpdateUserInfo,
            backToEmptyState = backToEmptyState,
            modifier = modifier
        )

        else -> {
            LoadingScreen()
        }
    }
}

@Composable
fun ProfileContent(
    data: AuthResponse,
    onUpdateProfile: (String?, String?, String?, String, String?, Context) -> Unit,
    updatedProfileState: UiState<AuthResponse>,
    onUploadImage: (imageUri: Uri, context: Context) -> Unit,
    uploadImageState: UiState<String>,
    onUpdateUserInfo: (email: String, password: String) -> Unit,
    backToEmptyState: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var editableName by remember { mutableStateOf(data.name) }
    var editableEmail by remember { mutableStateOf(data.email) }
    var editablePassword by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var editableAvatarUri by remember { mutableStateOf<Uri?>(null) }
    var currentAvatarUrl by remember(data.avatarUrl) { mutableStateOf(data.avatarUrl) }
    var uploadedAvatarUrl by remember { mutableStateOf<String?>(null) }
    val originalName = remember { mutableStateOf(data.name) }
    val originalEmail = remember { mutableStateOf(data.email) }
    val originalAvatarUrl = remember { mutableStateOf(data.avatarUrl) }
    var isNameValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isNewPasswordValid by remember { mutableStateOf(true) }
    var isCurrentPasswordValid by remember { mutableStateOf(true) }
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }
    val focusRequesterCurrentPassword = remember { FocusRequester() }

    val context = LocalContext.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            editableAvatarUri = uri
            uploadedAvatarUrl = null
        }
    )

    LaunchedEffect(editableAvatarUri) {
        editableAvatarUri?.let { uri ->
            onUploadImage(uri, context)
        }
    }

    LaunchedEffect(uploadImageState) {
        when (uploadImageState) {
            is UiState.Success -> {
                uploadedAvatarUrl = uploadImageState.data
                currentAvatarUrl = uploadImageState.data
                editableAvatarUri = null
                SnackbarController.sendEvent(SnackbarEvent(string(context, R.string.avatar_uploaded_successfully)))
            }

            is UiState.Error -> {
                SnackbarController.sendEvent(SnackbarEvent("Error upload avatar: ${uploadImageState.message}"))
                editableAvatarUri = null
            }

            else -> {}
        }
    }

    LaunchedEffect(updatedProfileState) {
        when (updatedProfileState) {
            is UiState.Success -> {
                SnackbarController.sendEvent(SnackbarEvent(string(context, R.string.profile_updated_successfully)))
                val finalEmail = updatedProfileState.data.email
                val finalPassword = if (editablePassword.trim().isNotEmpty()) editablePassword.trim() else currentPassword.trim()
                onUpdateUserInfo(finalEmail, finalPassword)
                isEditing = false
                backToEmptyState()
            }

            is UiState.Error -> {
                SnackbarController.sendEvent(SnackbarEvent("Update error: ${updatedProfileState.message}"))
                backToEmptyState()
            }

            else -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = !isEditing,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column {
                Card(shape = CircleShape) {
                    ProfileAvatar(
                        avatarUrl = currentAvatarUrl,
                        isUploading = false
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = data.name, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.email,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        ElevatedButton(
            shape = RoundedCornerShape(16.dp),
            onClick = {
                isEditing = !isEditing
                if (isEditing) {
                    editableName = originalName.value
                    editableEmail = originalEmail.value
                    currentAvatarUrl = originalAvatarUrl.value
                    editablePassword = ""
                    currentPassword = ""
                    editableAvatarUri = null
                    uploadedAvatarUrl = null
                    isNameValid = true
                    isEmailValid = true
                    isNewPasswordValid = true
                    isCurrentPasswordValid = true
                } else {
                    editableAvatarUri = null
                    uploadedAvatarUrl = null
                    currentAvatarUrl = originalAvatarUrl.value
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
        ) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Profile")
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isEditing) stringResource(R.string.close_profile_editing) else stringResource(R.string.edit_profile))
        }
        VerticalSpacer(16.dp)

        AnimatedVisibility(
            visible = isEditing,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
                    Text(
                        stringResource(R.string.profile_editing),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    VerticalSpacer(16.dp)

                    Box(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = CircleShape,
                        ) {
                            Box {
                                ProfileAvatar(
                                    avatarUrl = editableAvatarUri?.toString() ?: currentAvatarUrl,
                                    isUploading = uploadImageState is UiState.Loading
                                )
                                IconButton(
                                    onClick = {
                                        singlePhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(16.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                                        .size(36.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_edit_24),
                                        contentDescription = stringResource(R.string.edit_avatar),
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    VerticalSpacer(24.dp)

                    InputTextField(
                        labelResId = R.string.username,
                        placeholderResId = R.string.enter_your_name,
                        value = editableName,
                        onValueChange = { editableName = it; isNameValid = true },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusRequesterEmail.requestFocus() }),
                        focusRequester = focusRequesterName,
                        isError = !isNameValid,
                        errorMessage = if (!isNameValid) stringResource(R.string.name_invalid) else null
                    )
                    VerticalSpacer(8.dp)

                    InputTextField(
                        labelResId = R.string.email,
                        placeholderResId = R.string.enter_your_email,
                        value = editableEmail,
                        onValueChange = { editableEmail = it; isEmailValid = true },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusRequesterPassword.requestFocus() }),
                        focusRequester = focusRequesterEmail,
                        isError = !isEmailValid,
                        errorMessage = if (!isEmailValid) stringResource(R.string.email_invalid) else null
                    )
                    VerticalSpacer(8.dp)

                    InputTextField(
                        labelResId = R.string.new_password,
                        placeholderResId = R.string.enter_new_password_optional,
                        value = editablePassword,
                        onValueChange = { editablePassword = it; isNewPasswordValid = true },
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusRequesterCurrentPassword.requestFocus() }),
                        focusRequester = focusRequesterPassword,
                        trailingIcon = {
                            val image = if (newPasswordVisible) R.drawable.visibility else R.drawable.visibility_off
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(painter = painterResource(image), contentDescription = stringResource(R.string.toggle_password_visibility))
                            }
                        },
                        isError = !isNewPasswordValid,
                        errorMessage = if (!isNewPasswordValid) stringResource(R.string.password_invalid_length) else null
                    )
                    VerticalSpacer(8.dp)

                    InputTextField(
                        labelResId = R.string.current_password,
                        placeholderResId = R.string.enter_current_password_to_save,
                        value = currentPassword,
                        onValueChange = { currentPassword = it; isCurrentPasswordValid = true },
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { }),
                        focusRequester = focusRequesterCurrentPassword,
                        trailingIcon = {
                            val image = if (currentPasswordVisible) R.drawable.visibility else R.drawable.visibility_off
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(painter = painterResource(image), contentDescription = stringResource(R.string.toggle_password_visibility))
                            }
                        },
                        isError = !isCurrentPasswordValid,
                        errorMessage = if (!isCurrentPasswordValid) stringResource(R.string.current_password_required) else null
                    )
                    VerticalSpacer(16.dp)

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        ElevatedButton(
                            onClick = {
                                isNameValid = editableName.trim().length in 2..24
                                isEmailValid = Patterns.EMAIL_ADDRESS.matcher(editableEmail.trim()).matches()
                                isNewPasswordValid = editablePassword.isEmpty() || editablePassword.length in 8..24
                                isCurrentPasswordValid = currentPassword.isNotBlank()

                                if (isNameValid && isEmailValid && isNewPasswordValid && isCurrentPasswordValid) {
                                    val nameToSend = if (editableName != originalName.value) editableName.trim() else null
                                    val emailToSend = if (editableEmail != originalEmail.value) editableEmail.trim() else null
                                    val passwordToSend = if (editablePassword.isNotEmpty()) editablePassword.trim() else null
                                    val currentPasswordToSend = currentPassword.trim()
                                    val avatarUrlToSend = if (uploadedAvatarUrl != null && uploadedAvatarUrl != originalAvatarUrl.value) {
                                        uploadedAvatarUrl
                                    } else {
                                        null
                                    }

                                    onUpdateProfile(
                                        nameToSend,
                                        emailToSend,
                                        passwordToSend,
                                        currentPasswordToSend,
                                        avatarUrlToSend,
                                        context
                                    )
                                }
                            },
                            enabled = updatedProfileState !is UiState.Loading && uploadImageState !is UiState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
                        ) {
                            Text(stringResource(R.string.save_changes))
                        }
                    }

                    if (updatedProfileState is UiState.Loading) {
                        VerticalSpacer(8.dp)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileAvatar(
    avatarUrl: String?,
    isUploading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (avatarUrl == null && !isUploading) {
            Image(
                painterResource(R.drawable.defaultprofilephoto),
                contentDescription = stringResource(R.string.profile_avatar),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            SubcomposeAsyncImage(
                model = avatarUrl,
                contentDescription = stringResource(R.string.profile_avatar),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(40.dp))
                    }
                },
                error = {
                    Image(
                        painterResource(R.drawable.defaultprofilephoto),
                        contentDescription = stringResource(R.string.profile_avatar) + " Error",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            )
        }

        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.surface
                )
            }
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
    Text(stringResource(labelResId), style = MaterialTheme.typography.titleMedium)
    VerticalSpacer(4.dp)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        placeholder = {
            Text(stringResource(placeholderResId), color = Color.Gray)
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        isError = isError
    )
    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileContentPreview() {
    val mockData = AuthResponse(
        id = 1,
        name = "John Doe",
        password = "password123",
        email = "john.doe@example.com",
        avatarUrl = "https://via.placeholder.com/150",
        role = "user",
        coins = 150,
        verificationCode = "123456",
        isEmailVerified = true
    )
    ProfileContent(
        data = mockData,
        onUpdateProfile = { _, _, _, _, _, _ -> },
        updatedProfileState = UiState.Idle(),
        onUpdateUserInfo = { _, _ -> },
        backToEmptyState = {},
        onUploadImage = { _, _ -> },
        uploadImageState = UiState.Idle()
    )
}