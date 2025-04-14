package com.vangelnum.wisher.features.home.sendwish.selectdate.presentation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vangelnum.wisher.R

@Composable
fun RegenerateConfirmationDialog(
    onShowRegenerateConfirmationDialogChange: (Boolean) -> Unit,
    onRegenerateKey: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onShowRegenerateConfirmationDialogChange(false) },
        title = { Text(stringResource(R.string.regenerate_key_confirmation_dialog_title)) },
        text = { Text(stringResource(R.string.regenerate_key_confirmation_dialog_message)) },
        confirmButton = {
            TextButton(onClick = {
                onRegenerateKey()
                onShowRegenerateConfirmationDialogChange(false)
            }) {
                Text(stringResource(R.string.regenerate_key_confirmation_dialog_confirm_button))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onShowRegenerateConfirmationDialogChange(false)
            }) {
                Text(stringResource(R.string.regenerate_key_confirmation_dialog_dismiss_button))
            }
        }
    )
}