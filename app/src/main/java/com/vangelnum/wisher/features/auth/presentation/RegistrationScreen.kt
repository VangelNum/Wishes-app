package com.vangelnum.wisher.features.auth.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.features.auth.data.model.AuthResponse

@Composable
fun RegistrationScreen(
    registrationState: UiState<AuthResponse>,
    onBackToEmptyState: () -> Unit,
    onNavigateToMainScreen: () -> Unit,
    onNavigateToLoginPage:() -> Unit,
    onRegisterUser: (name: String, email: String, password: String) -> Unit
) {
    val name = rememberSaveable { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    when (registrationState) {
        is UiState.Error -> ErrorScreen(
            errorMessage = registrationState.message,
            buttonMessage = stringResource(R.string.back),
            onButtonClick = onBackToEmptyState
        )
        UiState.Idle -> RegistrationContent(
            name = name,
            email = email,
            password = password,
            passwordVisible = passwordVisible,
            registerUser = onRegisterUser,
            onNavigateToLoginPage = onNavigateToLoginPage
        )
        UiState.Loading -> LoadingScreen()
        is UiState.Success -> onNavigateToMainScreen()
    }
}

@Composable
fun RegistrationContent(
    modifier: Modifier = Modifier,
    name: MutableState<String>,
    email: MutableState<String>,
    password: MutableState<String>,
    passwordVisible: MutableState<Boolean>,
    onNavigateToLoginPage:()-> Unit,
    registerUser: (name: String, email: String, password: String) -> Unit
) {
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.wishes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(16.dp),
                    tint = Color.White
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
            style = MaterialTheme.typography.bodyMedium // Добавил стиль для консистентности
        )
        VerticalSpacer(16.dp)

        InputTextField(
            labelResId = R.string.username,
            placeholderResId = R.string.enter_your_name,
            state = name,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterEmail.requestFocus() }),
            focusRequester = focusRequesterName
        )
        VerticalSpacer(8.dp)

        InputTextField(
            labelResId = R.string.email,
            placeholderResId = R.string.enter_your_email,
            state = email,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterPassword.requestFocus() }),
            focusRequester = focusRequesterEmail
        )
        VerticalSpacer(8.dp)

        InputTextField(
            labelResId = R.string.password,
            placeholderResId = R.string.enter_password,
            state = password,
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                registerUser(name.value, email.value, password.value)
            }),
            focusRequester = focusRequesterPassword,
            trailingIcon = {
                val image = if (passwordVisible.value) R.drawable.visibility else R.drawable.visibility_off
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(painter = painterResource(image), contentDescription = null)
                }
            }
        )
        VerticalSpacer(8.dp)
        Text("Already have account?", modifier = Modifier.fillMaxWidth().align(Alignment.End).clickable {
            onNavigateToLoginPage()
        })
        VerticalSpacer(8.dp)
        Button(
            onClick = { registerUser(name.value, email.value, password.value) },
            modifier = Modifier
                .fillMaxWidth()
                .height(OutlinedTextFieldDefaults.MinHeight)
        ) {
            Text(
                stringResource(R.string.register),
                style = MaterialTheme.typography.titleLarge
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
    state: MutableState<String>,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    focusRequester: FocusRequester
) {
    Text(stringResource(labelResId), style = MaterialTheme.typography.titleMedium)
    VerticalSpacer(4.dp)
    OutlinedTextField(
        value = state.value,
        onValueChange = { state.value = it },
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
        trailingIcon = trailingIcon
    )
}