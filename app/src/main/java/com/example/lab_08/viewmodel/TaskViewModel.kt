package com.example.lab_08.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_08.data.Task
import com.example.lab_08.data.TaskDao
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    // Flujo de tareas CORREGIDO
    val tasks: StateFlow<List<Task>> = taskDao.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estado para el diálogo de edición
    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    // Tarea seleccionada para editar
    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    // Función para agregar tarea
    fun addTask(description: String) {
        if (description.isNotBlank()) {
            viewModelScope.launch {
                taskDao.insertTask(
                    Task(
                        title = "Tarea",
                        description = description
                    )
                )
            }
        }
    }

    // Función para alternar completado
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    // Función para eliminar tarea
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task.id)
        }
    }

    // Función para eliminar todas las tareas
    fun deleteAllTasks() {
        viewModelScope.launch {
            taskDao.deleteAllTasks()
        }
    }

    // Función para preparar edición
    fun prepareEditTask(task: Task) {
        _selectedTask.value = task
        _showEditDialog.value = true
    }

    // Función para actualizar tarea
    fun updateTask(newDescription: String) {
        viewModelScope.launch {
            val currentTask = _selectedTask.value
            currentTask?.let { task ->
                taskDao.updateTask(task.copy(description = newDescription))
            }
            cancelEdit()
        }
    }

    // Función para cancelar edición
    fun cancelEdit() {
        _showEditDialog.value = false
        _selectedTask.value = null
    }
}