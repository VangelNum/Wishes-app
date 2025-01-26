package com.vangelnum.wisher.features.auth.register.presentation

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState

@Composable
fun RegistrationScreen(
    onNavigateToLoginPage: () -> Unit,
    onNavigateToVerifyEmail: (email: String, password: String) -> Unit,
    onRegisterUser: (name: String, email: String, password: String) -> Unit,
    pendingRegistrationState: UiState<String>,
    onBackRegistrationState: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isNameValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }

    RegistrationContent(
        name = name,
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        isNameValid = isNameValid,
        isEmailValid = isEmailValid,
        isPasswordValid = isPasswordValid,
        onNameChange = { newName ->
            name = newName.trim()
        },
        onEmailChange = { newEmail ->
            email = newEmail.trim()
        },
        onPasswordChange = { newPassword ->
            password = newPassword.trim()
        },
        onPasswordVisibleChange = { newPasswordVisible ->
            passwordVisible = newPasswordVisible
        },
        onNavigateToLoginPage = onNavigateToLoginPage,
        onRegisterClick = {
            isNameValid = name.length in 2..24 && name.isNotBlank()
            isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            isPasswordValid = password.length in 8..24 && password.isNotBlank()

            if (isNameValid && isEmailValid && isPasswordValid) {
                onRegisterUser(name, email, password)
            }
        },
        pendingRegistrationState = pendingRegistrationState
    )

    LaunchedEffect(key1 = pendingRegistrationState) {
        if (pendingRegistrationState is UiState.Success) {
            onNavigateToVerifyEmail(email, password)
            onBackRegistrationState()
        }
    }
}

@Composable
fun RegistrationContent(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    password: String,
    passwordVisible: Boolean,
    isNameValid: Boolean,
    isEmailValid: Boolean,
    isPasswordValid: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onNavigateToLoginPage: () -> Unit,
    onRegisterClick: () -> Unit,
    pendingRegistrationState: UiState<String>
) {
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD4F6FF),
                    contentColor = Color(0xFF9694FF)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.wishes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(16.dp),
                )
            }
        }
        VerticalSpacer(12.dp)

        Text(
            stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        VerticalSpacer(4.dp)
        Text(
            stringResource(R.string.register_subtitle),
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
        VerticalSpacer(16.dp)

        InputTextField(
            labelResId = R.string.username,
            placeholderResId = R.string.enter_your_name,
            value = name,
            onValueChange = onNameChange,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterEmail.requestFocus() }),
            focusRequester = focusRequesterName,
            isError = !isNameValid,
            errorMessage = if (!isNameValid) stringResource(R.string.name_invalid) else null
        )
        VerticalSpacer(16.dp)

        InputTextField(
            labelResId = R.string.email,
            placeholderResId = R.string.enter_your_email,
            value = email,
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterPassword.requestFocus() }),
            focusRequester = focusRequesterEmail,
            isError = !isEmailValid,
            errorMessage = if (!isEmailValid) stringResource(R.string.email_invalid) else null
        )
        VerticalSpacer(16.dp)

        InputTextField(
            labelResId = R.string.password,
            placeholderResId = R.string.enter_password,
            value = password,
            onValueChange = onPasswordChange,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (isNameValid && isEmailValid && isPasswordValid) {
                    onRegisterClick()
                }
            }),
            focusRequester = focusRequesterPassword,
            trailingIcon = {
                val image =
                    if (passwordVisible) R.drawable.visibility else R.drawable.visibility_off
                IconButton(onClick = {
                    onPasswordVisibleChange(!passwordVisible)
                }) {
                    Icon(painter = painterResource(image), contentDescription = null)
                }
            },
            isError = !isPasswordValid,
            errorMessage = if (!isPasswordValid) stringResource(R.string.password_invalid) else null
        )
        VerticalSpacer(16.dp)
        Button(
            onClick = {
                onRegisterClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            enabled = pendingRegistrationState !is UiState.Loading
        ) {
            Text(
                stringResource(R.string.continue_string),
                style = MaterialTheme.typography.titleLarge
            )
            if (pendingRegistrationState is UiState.Loading) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        if (pendingRegistrationState is UiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = pendingRegistrationState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Text(stringResource(R.string.already_have_account), modifier = Modifier
                .padding(bottom = 16.dp)
                .clickable {
                    onNavigateToLoginPage()
                }
            )
        }
    }
}

@Composable
fun VerticalSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
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
        shape = CircleShape,
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