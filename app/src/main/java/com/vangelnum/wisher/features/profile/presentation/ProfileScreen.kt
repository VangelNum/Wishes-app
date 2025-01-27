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
import androidx.compose.material3.Button
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
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.features.auth.register.presentation.VerticalSpacer

@Composable
fun ProfileScreen(
    userInfoState: UiState<AuthResponse>,
    onUpdateProfile: (String?, String?, String?, String?, Uri?, Context) -> Unit,
    updatedProfileState: UiState<AuthResponse>,
    onUpdateUserInfo: (email: String, password: String) -> Unit,
    backToEmptyState: ()->Unit,
    modifier: Modifier = Modifier
) {
    when (userInfoState) {
        is UiState.Error -> {
            ErrorScreen("Ошибка загрузки профиля")
        }

        is UiState.Idle -> {}
        is UiState.Loading -> {
            LoadingScreen("Загружаем информацию о профиле")
        }

        is UiState.Success -> {
            ProfileContent(
                data = userInfoState.data,
                onUpdateProfile = onUpdateProfile,
                updatedProfileState = updatedProfileState,
                onUpdateUserInfo = onUpdateUserInfo,
                backToEmptyState = backToEmptyState,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ProfileContent(
    data: AuthResponse,
    onUpdateProfile: (String?, String?, String?, String?, Uri?, Context) -> Unit,
    updatedProfileState: UiState<AuthResponse>,
    onUpdateUserInfo: (email: String, password: String) -> Unit,
    backToEmptyState: ()->Unit,
    modifier: Modifier = Modifier
) {

    var isEditing by remember { mutableStateOf(false) }
    var editableName by remember { mutableStateOf(data.name) }
    var editableEmail by remember { mutableStateOf(data.email) }
    var editablePassword by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var editableAvatarUri by remember { mutableStateOf<Uri?>(null) }
    var currentAvatarUrl by remember { mutableStateOf(data.avatarUrl) }

    val originalName = remember { mutableStateOf(data.name) }
    val originalEmail = remember { mutableStateOf(data.email) }
    val originalAvatarUrl = remember { mutableStateOf(data.avatarUrl) }

    var isNameValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var isCurrentPasswordValid by remember { mutableStateOf(true) }

    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }
    val focusRequesterCurrentPassword = remember { FocusRequester() }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            editableAvatarUri = uri
        }
    )

    val context = LocalContext.current

    LaunchedEffect(updatedProfileState) {
        if (updatedProfileState is UiState.Success) {
            if (editablePassword.isEmpty()) {
                onUpdateUserInfo(editableEmail, currentPassword)
            } else {
                onUpdateUserInfo(editableEmail, editablePassword)
            }
            backToEmptyState()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = !isEditing,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    shape = CircleShape
                ) {
                    ProfileAvatar(avatarUrl = currentAvatarUrl)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = data.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
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
                    editableName = data.name
                    editableEmail = data.email
                    editablePassword = ""
                    currentPassword = ""
                    editableAvatarUri = null
                    currentAvatarUrl = data.avatarUrl
                    originalName.value = data.name
                    originalEmail.value = data.email
                    originalAvatarUrl.value = data.avatarUrl
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
        ) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Profile")
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isEditing) "Закрыть редактирование профиля" else "Редактировать профиль")
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Редактирование профиля",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    VerticalSpacer(16.dp)

                    Card(
                        shape = CircleShape,
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Box {
                            ProfileAvatar(
                                avatarUrl = editableAvatarUri?.toString() ?: currentAvatarUrl
                            )
                            IconButton(
                                onClick = {
                                    singlePhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_edit_24),
                                    contentDescription = "Edit Avatar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    VerticalSpacer(16.dp)


                    InputTextField(
                        labelResId = R.string.username,
                        placeholderResId = R.string.enter_your_name,
                        value = editableName,
                        onValueChange = { editableName = it },
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
                        onValueChange = { editableEmail = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusRequesterPassword.requestFocus() }),
                        focusRequester = focusRequesterEmail,
                        isError = !isEmailValid,
                        errorMessage = if (!isEmailValid) stringResource(R.string.email_invalid) else null
                    )

                    VerticalSpacer(8.dp)

                    InputTextField(
                        labelResId = R.string.new_password,
                        placeholderResId = R.string.enter_new_password,
                        value = editablePassword,
                        onValueChange = { editablePassword = it },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusRequesterCurrentPassword.requestFocus() }),
                        focusRequester = focusRequesterPassword,
                        trailingIcon = {
                            val image =
                                if (passwordVisible) R.drawable.visibility else R.drawable.visibility_off
                            IconButton(onClick = {
                                passwordVisible = !passwordVisible
                            }) {
                                Icon(painter = painterResource(image), contentDescription = null)
                            }
                        },
                        isError = !isPasswordValid,
                        errorMessage = if (!isPasswordValid) stringResource(R.string.password_invalid) else null // Reusing password invalid string
                    )
                    VerticalSpacer(8.dp)

                    InputTextField(
                        labelResId = R.string.current_password,
                        placeholderResId = R.string.enter_current_password,
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (isNameValid && isEmailValid && isPasswordValid && isCurrentPasswordValid) {
                                val updatedName =
                                    if (editableName != originalName.value) editableName else null
                                val updatedEmail =
                                    if (editableEmail != originalEmail.value) editableEmail else null
                                val updatedPassword =
                                    if (editablePassword.isNotEmpty()) editablePassword else null
                                val updatedAvatarUri =
                                    if (editableAvatarUri?.toString() != originalAvatarUrl.value || editableAvatarUri != null && originalAvatarUrl.value == null) editableAvatarUri else null
                                onUpdateProfile(
                                    updatedName,
                                    updatedEmail,
                                    updatedPassword,
                                    currentPassword,
                                    updatedAvatarUri,
                                    context
                                )
                            }
                        }),
                        focusRequester = focusRequesterCurrentPassword,
                        trailingIcon = {
                            val image =
                                if (currentPasswordVisible) R.drawable.visibility else R.drawable.visibility_off
                            IconButton(onClick = {
                                currentPasswordVisible = !currentPasswordVisible
                            }) {
                                Icon(painter = painterResource(image), contentDescription = null)
                            }
                        },
                        isError = !isCurrentPasswordValid,
                        errorMessage = if (!isCurrentPasswordValid) stringResource(R.string.password_invalid) else null // Reusing password invalid string
                    )
                    VerticalSpacer(16.dp)

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Button(
                            onClick = {
                                isNameValid =
                                    editableName.length in 2..24 && editableName.isNotBlank()
                                isEmailValid =
                                    Patterns.EMAIL_ADDRESS.matcher(editableEmail).matches()
                                isPasswordValid =
                                    editablePassword.isEmpty() || (editablePassword.length in 8..24 && editablePassword.isNotBlank())
                                isCurrentPasswordValid = currentPassword.isNotBlank()

                                if (isNameValid && isEmailValid && isPasswordValid && isCurrentPasswordValid) {
                                    val updatedName =
                                        if (editableName != originalName.value) editableName else null
                                    val updatedEmail =
                                        if (editableEmail != originalEmail.value) editableEmail else null
                                    val updatedPassword =
                                        if (editablePassword.isNotEmpty()) editablePassword else null
                                    val updatedAvatarUri =
                                        if (editableAvatarUri?.toString() != originalAvatarUrl.value || editableAvatarUri != null && originalAvatarUrl.value == null) editableAvatarUri else null

                                    onUpdateProfile(
                                        updatedName,
                                        updatedEmail,
                                        updatedPassword,
                                        currentPassword,
                                        updatedAvatarUri,
                                        context
                                    )
                                    currentAvatarUrl =
                                        editableAvatarUri?.toString() ?: currentAvatarUrl
                                }
                            },
                            enabled = true
                        ) {
                            Text("Сохранить")
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
                    if (updatedProfileState is UiState.Error) {
                        VerticalSpacer(8.dp)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorScreen(updatedProfileState.message)
                        }
                    }
                    if (updatedProfileState is UiState.Success) {
                        VerticalSpacer(8.dp)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.profile_updated_successfully),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileAvatar(avatarUrl: String?) {
    if (avatarUrl == null) {
        Image(
            painterResource(R.drawable.defaultprofilephoto),
            contentDescription = "Profile Avatar",
            modifier = Modifier
                .size(120.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        SubcomposeAsyncImage(
            model = avatarUrl,
            contentDescription = "Profile Avatar",
            modifier = Modifier
                .size(120.dp),
            contentScale = ContentScale.Crop,
            loading = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
        )
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
        backToEmptyState = {}
    )
}