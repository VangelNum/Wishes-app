package com.vangelnum.wisher.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

@Composable
fun RegistrationScreen(
    navController: NavHostController,
    viewModel: RegistrationViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                }
                is UiEvent.Failure -> {
                    // Handle registration failure (e.g., show a snackbar)
                }
            }
        }
    }

    RegistrationScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onRegisterClick = viewModel::registerUser
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreenContent(
    state: RegistrationState,
    onEvent: (RegistrationEvent) -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Registration")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = { onEvent(RegistrationEvent.NameChanged(it)) },
            label = { Text("Name") },
            isError = state.nameError != null
        )
        if (state.nameError != null) {
            Text(text = state.nameError, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { onEvent(RegistrationEvent.EmailChanged(it)) },
            label = { Text("Email") },
            isError = state.emailError != null
        )
        if (state.emailError != null) {
            Text(text = state.emailError, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { onEvent(RegistrationEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            isError = state.passwordError != null
        )
        if (state.passwordError != null) {
            Text(text = state.passwordError, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        state.avatarUrl?.let {
            OutlinedTextField(
                value = it,
                onValueChange = { onEvent(RegistrationEvent.AvatarUrlChanged(it)) },
                label = { Text("Avatar URL (optional)") }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRegisterClick, enabled = !state.isLoading) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Register")
            }
        }
    }
}