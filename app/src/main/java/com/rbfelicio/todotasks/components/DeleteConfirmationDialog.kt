package com.rbfelicio.todotasks.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.error

@Composable
fun DeleteConfirmationDialog(
    taskTitle: String,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Confirmar Exclusão") },
        text = { Text("Tem certeza de que deseja excluir a tarefa \"$taskTitle\"?") },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmClick()
                    onDismissRequest() // Fecha o diálogo após confirmar
                }
            ) {
                Text("Excluir", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}
