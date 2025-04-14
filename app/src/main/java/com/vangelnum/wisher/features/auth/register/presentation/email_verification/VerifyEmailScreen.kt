package com.vangelnum.wisher.features.auth.register.presentation.email_verification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.core.presentation.SnackbarController
import com.vangelnum.wisher.core.presentation.SnackbarEvent
import com.vangelnum.wisher.features.auth.core.model.AuthResponse
import com.vangelnum.wisher.ui.theme.WisherappTheme
import kotlinx.coroutines.delay

@Composable
fun VerifyEmailScreen(
    email: String,
    registrationState: UiState<AuthResponse>,
    otpState: OtpState,
    focusRequesters: List<FocusRequester>,
    onAction: (OtpAction) -> Unit,
    onNavigateUploadAvatarScreen: () -> Unit,
    resendVerificationCodeUiState: String?,
    resendVerificationCode: () -> Unit
) {
    LaunchedEffect(key1 = registrationState) {
        if (registrationState is UiState.Success) {
            onNavigateUploadAvatarScreen()
        }
        if (registrationState is UiState.Error) {
            SnackbarController.sendEvent(SnackbarEvent(registrationState.message))
        }
    }

    LaunchedEffect(resendVerificationCodeUiState) {
        if (resendVerificationCodeUiState != null) {
            SnackbarController.sendEvent(SnackbarEvent(resendVerificationCodeUiState))
        }
    }

    var secondsLeft by rememberSaveable { mutableStateOf(60) }
    val isTimerRunning = secondsLeft > 0

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft--
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.verify_email_message, email),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.verify_email_enter_code),
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
        Button(
            onClick = {
                if (!isTimerRunning) {
                    secondsLeft = 60
                    resendVerificationCode()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            enabled = !isTimerRunning,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (isTimerRunning) {
                    stringResource(R.string.resend_verification_code_timer, secondsLeft)
                } else {
                    stringResource(R.string.resend_verification_code)
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (registrationState is UiState.Loading) {
            LoadingScreen(loadingText = stringResource(R.string.verifying_code_loading))
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
fun VerifyEmailScreenPreview() {
    val focusRequesters = remember {
        List(6) { FocusRequester() }
    }
    VerifyEmailScreen(
        email = "ena@mail.ru",
        registrationState = UiState.Error("SOME"),
        otpState = OtpState(),
        focusRequesters = focusRequesters,
        onAction = {},
        onNavigateUploadAvatarScreen = {},
        resendVerificationCode = {},
        resendVerificationCodeUiState = null,
    )
}