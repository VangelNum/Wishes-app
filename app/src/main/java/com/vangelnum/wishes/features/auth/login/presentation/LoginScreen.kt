package com.vangelnum.wishes.features.auth.login.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.presentation.SmallLoadingIndicator
import com.vangelnum.wishes.core.presentation.SnackbarController
import com.vangelnum.wishes.core.presentation.SnackbarEvent
import com.vangelnum.wishes.features.auth.core.model.AuthResponse
import com.vangelnum.wishes.features.auth.register.presentation.VerticalSpacer

@Composable
fun LoginScreen(
    loginState: UiState<AuthResponse>,
    onNavigateToHomeScreen: () -> Unit,
    onEvent: (LoginEvent) -> Unit,
    onNavigateToRegisterScreen: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }

    LaunchedEffect(loginState) {
        if (loginState is UiState.Error) {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = loginState.message
                )
            )
            onEvent(LoginEvent.OnBackToEmptyState)
        }
        if (loginState is UiState.Success) {
            onNavigateToHomeScreen()
        }
    }

    Column(
        modifier = Modifier
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
            stringResource(R.string.login),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        VerticalSpacer(4.dp)
        Text(
            stringResource(R.string.login_to_countinue),
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
        VerticalSpacer(16.dp)

        InputLoginTextField(
            labelResId = R.string.email,
            placeholderResId = R.string.enter_your_email,
            state = email,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterPassword.requestFocus() }),
            focusRequester = focusRequesterEmail
        )
        VerticalSpacer(16.dp)

        InputLoginTextField(
            labelResId = R.string.password,
            placeholderResId = R.string.enter_password,
            state = password,
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                onEvent(LoginEvent.OnLoginUser(email.value, password.value))
            }),
            focusRequester = focusRequesterPassword,
            trailingIcon = {
                val image =
                    if (passwordVisible.value) R.drawable.visibility else R.drawable.visibility_off
                IconButton(onClick = {
                    passwordVisible.value = !passwordVisible.value
                }) {
                    Icon(painter = painterResource(image), contentDescription = null)
                }
            }
        )
        VerticalSpacer(16.dp)
        Button(
            onClick = {
                onEvent(LoginEvent.OnLoginUser(email.value, password.value))
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            enabled = loginState !is UiState.Loading
        ) {
            Text(
                stringResource(R.string.login),
                style = MaterialTheme.typography.titleLarge
            )
            if (loginState is UiState.Loading) {
                Spacer(modifier = Modifier.width(16.dp))
                SmallLoadingIndicator()
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                stringResource(R.string.dont_have_an_account), modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clickable {
                        onNavigateToRegisterScreen()
                    })
        }
    }
}


@Composable
fun InputLoginTextField(
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
        onValueChange = { state.value = it.trim() },
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