package com.rbfelicio.todotasks.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import com.rbfelicio.todotasks.R

@Composable
fun DeleteConfirmationDialog(
    taskTitle: String,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(id = R.string.delete_confirmation_dialog_title)) },
        text = { Text(stringResource(id = R.string.delete_confirmation_dialog_message, taskTitle)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmClick()
                    onDismissRequest() // Fecha o diálogo após confirmar
                }
            ) {
                Text(stringResource(id = R.string.dialog_button_delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.dialog_button_cancel))
            }
        }
    )
}
