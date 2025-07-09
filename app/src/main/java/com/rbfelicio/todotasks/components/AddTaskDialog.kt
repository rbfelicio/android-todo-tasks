package com.rbfelicio.todotasks.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rbfelicio.todotasks.Task

@Composable
fun AddTaskDialog(
    existingTask: Task? = null,
    onDismissRequest: () -> Unit,
    onConfirmClick: (title: String, description: String, id: Int?) -> Unit
) {
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    LaunchedEffect(existingTask) {
        if (existingTask != null) {
            title = existingTask.title
            description = existingTask.description ?: ""
        } else {
            // Se for para adicionar uma nova, limpar os campos
            // (geralmente não necessário se o diálogo é recriado, mas bom para robustez)
            // title = ""
            // description = ""
        }
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Adicionar Nova Tarefa") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição (Opcional)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirmClick(title, description, existingTask?.id)
                    }
                }
            ) {
                Text(if (existingTask == null) "Adicionar" else "Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}