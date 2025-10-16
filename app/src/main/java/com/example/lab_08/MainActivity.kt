package com.example.lab_08



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab_08.data.TaskDatabase
import com.example.lab_08.ui.theme.Lab_08Theme
import com.example.lab_08.viewmodel.TaskViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState // Si usas LiveData
// O si usas StateFlow:
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
        // Header
        Text(
            text = "Mi Lista de Tareas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input para nueva tarea
        OutlinedTextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("¿Qué necesitas hacer?") },
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
            Text("Agregar Tarea")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de tareas
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
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                        onEdit = { viewModel.prepareEditTask(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }

        // Botón eliminar todas las tareas
        if (tasks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { viewModel.deleteAllTasks() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar Todas las Tareas")
            }
        }

        // Diálogo de edición
        EditTaskDialog(viewModel = viewModel)
    }
}

@Composable
fun TaskItem(
    task: com.example.lab_08.data.Task,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if (task.isCompleted) {
                    Text(
                        text = "Completada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row {
                // Botón editar
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }

                // Botón completar/pendiente
                Button(
                    onClick = onToggleComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (task.isCompleted) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                ) {
                    Text(if (task.isCompleted) "Hecha" else "Pendiente")
                }

                // Botón eliminar
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}

@Composable
fun EditTaskDialog(viewModel: TaskViewModel) {
    val showDialog by viewModel.showEditDialog.collectAsState()
    val selectedTask by viewModel.selectedTask.collectAsState()
    var editedText by remember { mutableStateOf("") }

    // Actualizar el texto cuando se selecciona una tarea
    LaunchedEffect(selectedTask) {
        selectedTask?.let {
            editedText = it.description
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelEdit() },
            title = { Text("Editar Tarea") },
            text = {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    label = { Text("Descripción de la tarea") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.updateTask(editedText) },
                    enabled = editedText.isNotBlank()
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelEdit() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}