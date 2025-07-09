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
import androidx.compose.ui.res.stringResource
import com.rbfelicio.todotasks.R
import com.rbfelicio.todotasks.data.Task

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
            title = ""
            description = ""
        }
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(
            stringResource(
            if (existingTask == null) R.string.add_task_dialog_title_add
            else R.string.add_task_dialog_title_edit
        )
        ) },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(id = R.string.add_task_dialog_label_title)) },
                    singleLine = true
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(id = R.string.add_task_dialog_label_description)) }
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
                Text(
                    stringResource(
                        if (existingTask == null) R.string.add_task_dialog_button_add
                        else R.string.add_task_dialog_button_save
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.dialog_button_cancel))
            }
        }
    )
}