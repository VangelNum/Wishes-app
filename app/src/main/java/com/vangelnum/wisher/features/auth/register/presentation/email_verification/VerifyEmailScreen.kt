package com.vangelnum.wisher.features.auth.register.presentation.email_verification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.ui.theme.WisherappTheme

@Composable
fun VerifyEmailScreen(
    email: String,
    registrationState: UiState<AuthResponse>,
    otpState: OtpState,
    focusRequesters: List<FocusRequester>,
    onAction: (OtpAction) -> Unit,
    onNavigateUploadAvatarScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {

    LaunchedEffect(key1 = registrationState) {
        if (registrationState is UiState.Success) {
            onNavigateUploadAvatarScreen()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "На вашу почту $email был отправлен код",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Введите  его ниже",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            otpState.code.forEachIndexed { index, number ->
                OtpInputField(
                    number = number,
                    focusRequester = focusRequesters[index],
                    onFocusChanged = { isFocused ->
                        if (isFocused) {
                            onAction(OtpAction.OnChangeFieldFocused(index))
                        }
                    },
                    onNumberChanged = { newNumber ->
                        onAction(OtpAction.OnEnterNumber(newNumber, index))
                    },
                    onKeyboardBack = {
                        onAction(OtpAction.OnKeyboardBack)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (registrationState) {
            is UiState.Loading -> {
                LoadingScreen(loadingText = "Проверяем код..")
            }

            is UiState.Error -> {
                ErrorScreen(errorMessage = registrationState.message)
            }

            else -> {}
        }
    }
}

@Preview
@Composable
private fun OtpInputFieldPreview() {
    WisherappTheme {
        OtpInputField(
            number = null,
            focusRequester = remember { FocusRequester() },
            onFocusChanged = {},
            onKeyboardBack = {},
            onNumberChanged = {},
            modifier = Modifier
                .size(100.dp)
        )
    }
}

@Composable
@Preview
fun VerifyEmailScreenPreview(modifier: Modifier = Modifier) {
    val focusRequesters = remember {
        List(6) { FocusRequester() }
    }
    VerifyEmailScreen(
        email = "ena@mail.ru",
        registrationState = UiState.Error("SOME"),
        otpState = OtpState(),
        focusRequesters = focusRequesters,
        onAction = {},
        onNavigateUploadAvatarScreen = {}
    )
}