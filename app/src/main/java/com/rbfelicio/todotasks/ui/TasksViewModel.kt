package com.rbfelicio.todotasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbfelicio.todotasks.data.Task
import com.rbfelicio.todotasks.data.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class TasksViewModel @Inject constructor(
    private val repository: TasksRepository
) : ViewModel() {

    val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.getAllTasks()
                .catch { exception ->
                    _tasks.value = emptyList()
                }
                .collect { taskList ->
                    _tasks.value = taskList
                }
        }
    }

    fun addTask(title: String, description: String?) {
        viewModelScope.launch {
            val newTask = Task(title = title, description = description?.ifBlank { null })
            repository.insertTask(newTask)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun toggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            repository.updateTask(updatedTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}
