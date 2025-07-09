package com.rbfelicio.todotasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rbfelicio.todotasks.components.AddTaskDialog
import com.rbfelicio.todotasks.ui.theme.ToDoTasksTheme

data class Task(
    val id: Int,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTasksTheme {
                TodoListApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListApp() {
    // Estado para armazenar a lista de tarefas (inicialmente vazia)
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Tarefas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Filled.Add, "Adicionar nova tarefa")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (tasks.isEmpty()) {
                EmptyTasksView()
            } else {
                TaskList(
                    tasks = tasks,
                    onTaskCheckedChanged = { taskToUpdate, isCompleted ->
                        // Lógica para atualizar a tarefa na lista
                        tasks = tasks.map { currentTask ->
                            if (currentTask.id == taskToUpdate.id) {
                                currentTask.copy(isCompleted = isCompleted)
                            } else {
                                currentTask
                            }
                        }
                    },
                    onTaskClick = { task ->
                        taskToEdit = task
                    }
                )
            }

            if (showDialog || taskToEdit != null) {
                AddTaskDialog(
                    existingTask = taskToEdit,
                    onDismissRequest = {
                        showDialog = false
                        taskToEdit = null
                    },
                    onConfirmClick = { title, description, id ->
                        if (id != null) {
                            tasks = tasks.map {
                                if (it.id == id) {
                                    it.copy(
                                        title = title,
                                        description = description.ifBlank { null })
                                } else {
                                    it
                                }
                            }
                        } else {
                            val newTaskId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
                            val newTask = Task(
                                id = newTaskId,
                                title = title,
                                description = description.ifBlank { null }
                            )
                            tasks = tasks + newTask
                        }
                        showDialog = false
                        taskToEdit = null
                    }
                )
            }
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskCheckedChanged: (Task, Boolean) -> Unit,
    onTaskClick: (Task) -> Unit // Adicionar este parâmetro
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Espaçamento entre os cards
    ) {
        items(tasks, key = { task -> task.id }) { task ->
            TaskItem(
                task = task,
                onTaskCheckedChanged = onTaskCheckedChanged,
                onTaskClick = onTaskClick
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskCheckedChanged: (Task, Boolean) -> Unit,
    onTaskClick: (Task) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // Modificado de fillMaxSize
            .clickable { onTaskClick(task) }, // Tornar o Card clicável
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row( // Usar Row para alinhar Checkbox e o conteúdo do texto
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), // Para que a Row ocupe a largura do Card
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { isChecked ->
                    onTaskCheckedChanged(task, isChecked)
                }
            )
            Spacer(modifier = Modifier.width(8.dp)) // Espaçamento entre Checkbox e texto
            Column(
                modifier = Modifier.weight(1f) // Dar peso para a coluna ocupar o espaço restante
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )
                task.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Cor um pouco mais clara
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    )
                }
            }
            Icon(Icons.Filled.Edit, contentDescription = "Editar Tarefa")
        }
    }
}

@Composable
fun EmptyTasksView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Você não possui atividades.\nComece criando uma agora!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f) // Cor um pouco mais clara
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ToDoTasksTheme {
        TodoListApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EmptyTasksPreview() {
    ToDoTasksTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Minhas Tarefas") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /*TODO*/ },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Filled.Add, "Adicionar nova tarefa")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                EmptyTasksView()
            }
        }
    }
}