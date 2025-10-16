package com.example.lab_08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab_08.data.TaskDatabase
import com.example.lab_08.ui.theme.Lab_08Theme
import com.example.lab_08.viewmodel.TaskViewModel
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {

    private val database by lazy { TaskDatabase.getDatabase(this) }

    private val viewModel by viewModels<TaskViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskViewModel(database.taskDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab_08Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun TaskApp(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    var newTaskDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Input para nueva tarea
        OutlinedTextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Nueva tarea") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón agregar tarea
        Button(
            onClick = {
                viewModel.addTask(newTaskDescription)
                newTaskDescription = ""
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = newTaskDescription.isNotBlank()
        ) {
            Text("Agregar tarea")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de tareas + Botón eliminar todo
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay tareas pendientes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Lista de tareas individuales
                tasks.forEach { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskCompletion(task) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Botón Eliminar todas las tareas - como cuarto elemento
                Button(
                    onClick = { viewModel.deleteAllTasks() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar todas las tareas")
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: com.example.lab_08.data.Task,
    onToggleComplete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // Botón con ancho fijo para mantener "Pendiente" en una línea
            Button(
                onClick = onToggleComplete,
                modifier = Modifier.width(110.dp) // Ancho fijo para texto responsive
            ) {
                Text(
                    text = "Pendiente",
                    maxLines = 1
                )
            }
        }
    }
}