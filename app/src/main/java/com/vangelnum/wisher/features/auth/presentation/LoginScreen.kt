package com.vangelnum.wisher.features.auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by loginViewModel.loginUiState.collectAsState()

    when (val state = loginState) {
        is UiState.Error -> {
            ErrorScreen(errorMessage = state.message, onButtonClick = {
                loginViewModel.backToEmptyState()
            }, buttonMessage = stringResource(R.string.back))
        }
        UiState.Loading -> {
            LoadingScreen()
        }
        is UiState.Success -> {
            Text(state.data.toString())
        }
        UiState.Idle -> {
            var email by remember {
                mutableStateOf("")
            }
            var password by remember {
                mutableStateOf("")
            }
            Column {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    }
                )
                Button(
                    onClick = {
                        loginViewModel.fetchUserInfo(email, password)
                    }
                ) {
                    Text("Continue")
                }
            }
        }
    }
}
