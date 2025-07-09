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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rbfelicio.todotasks.components.AddTaskDialog
import com.rbfelicio.todotasks.components.DeleteConfirmationDialog
import com.rbfelicio.todotasks.data.Task
import com.rbfelicio.todotasks.data.TaskDao
import com.rbfelicio.todotasks.data.TasksRepository
import com.rbfelicio.todotasks.ui.TasksViewModel
import com.rbfelicio.todotasks.ui.theme.ToDoTasksTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTasksTheme {
                val tasksViewModel: TasksViewModel = hiltViewModel()
                TodoListApp(viewModel = tasksViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListApp(viewModel: TasksViewModel) {
    // Estado para armazenar a lista de tarefas (inicialmente vazia)
    val tasks by viewModel.tasks.collectAsState()
    var showTaskInputDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }


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
                    showTaskInputDialog = true
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
                    onTaskCheckedChanged = { task ->
                        viewModel.toggleTaskCompleted(task)
                    },
                    onTaskClick = { task ->
                        taskToEdit = task
                        showTaskInputDialog = true
                    },
                    onDeleteClick = { task -> // Quando o ícone de lixeira é clicado
                        taskToDelete = task
                        showDeleteConfirmDialog = true
                    }
                )
            }

            if (showTaskInputDialog || taskToEdit != null) {
                AddTaskDialog(
                    existingTask = taskToEdit,
                    onDismissRequest = {
                        showTaskInputDialog = false
                        taskToEdit = null
                    },
                    onConfirmClick = { title, description, id ->
                        if (id != null && taskToEdit != null) { // Editando tarefa existente
                            // Criar uma cópia da tarefa com as novas informações, mantendo o ID e isCompleted
                            val updatedTask = taskToEdit!!.copy(
                                title = title,
                                description = description.ifBlank { null }
                                // `isCompleted` e `id` são preservados de `taskToEdit`
                            )
                            viewModel.updateTask(updatedTask)
                        } else { // Adicionando nova tarefa
                            viewModel.addTask(title, description.ifBlank { null })
                        }
                        showTaskInputDialog = false
                        taskToEdit = null
                    }
                )
            }
            if (showDeleteConfirmDialog && taskToDelete != null) {
                DeleteConfirmationDialog(
                    taskTitle = taskToDelete!!.title, // Usamos !! pois showDeleteConfirmDialog só é true se taskToDelete não for null
                    onDismissRequest = {
                        showDeleteConfirmDialog = false
                        taskToDelete = null
                    },
                    onConfirmClick = {
                        viewModel.deleteTask(taskToDelete!!)
                        showDeleteConfirmDialog = false
                        taskToDelete = null
                    }
                )
            }
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskCheckedChanged: (Task) -> Unit,
    onTaskClick: (Task) -> Unit,
    onDeleteClick: (Task) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Espaçamento entre os cards
    ) {
        items(tasks, key = { task -> task.id }) { task ->
            TaskItem(
                task = task,
                onTaskCheckedChanged = onTaskCheckedChanged, // Passa a lambda
                onTaskClick = onTaskClick,             // Passa a lambda
                onDeleteClick = onDeleteClick          // Passa a lambda
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskCheckedChanged: (Task) -> Unit,
    onTaskClick: (Task) -> Unit,
    onDeleteClick: (Task) -> Unit
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
                onCheckedChange = { _ ->
                    onTaskCheckedChanged(task)
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
            IconButton(onClick = { onDeleteClick(task) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Excluir Tarefa",
                    tint = MaterialTheme.colorScheme.error // Usar uma cor que indique perigo/erro
                )
            }
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
        class PreviewTasksViewModel : TasksViewModel(
            TasksRepository( // O TasksRepository precisa de um TaskDao
                object : TaskDao { // Este é o nosso stub para TaskDao
                    override fun getAllTasks(): Flow<List<Task>> {
                        // Retorna um Flow com uma lista de tarefas de exemplo para o preview
                        return flowOf(
                            listOf(
                                Task(
                                    id = 1,
                                    title = "Comprar pão",
                                    description = "Na padaria da esquina",
                                    isCompleted = false
                                ),
                                Task(id = 2, title = "Lavar o carro", isCompleted = true),
                                Task(
                                    id = 3,
                                    title = "Ler documentação do Compose",
                                    description = "Capítulos 1 ao 5"
                                )
                            )
                        )
                    }

                    override suspend fun getTaskById(taskId: Int): Task? {
                        // Para preview, pode retornar null ou uma tarefa específica se necessário
                        if (taskId == 1) {
                            return Task(
                                id = 1,
                                title = "Comprar pão",
                                description = "Na padaria da esquina",
                                isCompleted = false
                            )
                        }
                        return null
                    }

                    override suspend fun insertTask(task: Task): Long {
                        // Em um stub, geralmente não fazemos nada ou apenas logamos
                        println("PreviewTaskDao: insertTask chamada com $task")
                        return task.id.toLong() // Pode retornar um ID de exemplo
                    }

                    override suspend fun updateTask(task: Task) {
                        // Em um stub, geralmente não fazemos nada ou apenas logamos
                        println("PreviewTaskDao: updateTask chamada com $task")
                    }

                    override suspend fun deleteTask(task: Task) {
                        // Em um stub, geralmente não fazemos nada ou apenas logamos
                        println("PreviewTaskDao: deleteTask chamada com $task")
                    }
                }
            )
        ) {
            init {
                // Forçar um estado inicial para o preview se necessário
                _tasks.value = listOf(Task(1, "Preview Task 1"))
            }
        }
        TodoListApp(PreviewTasksViewModel())
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