package com.example.lab_08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    color = Color(0xFF1E1E2E)
                ) {
                    ModernTaskApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun ModernTaskApp(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    var newTaskDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // HEADER - EN ESPAÑOL
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HOY",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA0A0A0),
                    fontSize = 12.sp
                )
                Text(
                    text = "MIS TAREAS", // CAMBIADO DE LOREM IPSUM
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(
                onClick = { /* Menú */ }
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // STATS CARDS - EN ESPAÑOL
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "TAREAS",
                value = tasks.size.toString(),
                color = Color(0xFF4A6572),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "COMPLETADAS",
                value = tasks.count { it.isCompleted }.toString(),
                color = Color(0xFF4A6572),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "PENDIENTES",
                value = tasks.count { !it.isCompleted }.toString(),
                color = Color(0xFF4A6572),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TASK LIST HEADER - EN ESPAÑOL
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MIS TAREAS",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${tasks.size} tareas", // EN ESPAÑOL
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFA0A0A0)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TASK LIST
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No hay tareas aún", // EN ESPAÑOL
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFA0A0A0)
                    )
                    Text(
                        "Agrega tu primera tarea abajo", // EN ESPAÑOL
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA0A0A0)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks) { task ->
                    ModernTaskItem(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                        onEdit = { viewModel.prepareEditTask(task) }, // AGREGADO EDITAR
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ADD TASK SECTION - EN ESPAÑOL
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2D3748)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "AGREGAR NUEVA TAREA", // EN ESPAÑOL
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTaskDescription,
                        onValueChange = { newTaskDescription = it },
                        placeholder = { Text("Descripción de la tarea...", color = Color(0xFFA0A0A0)) }, // EN ESPAÑOL
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF1E1E2E),
                            unfocusedContainerColor = Color(0xFF1E1E2E),
                            focusedIndicatorColor = Color(0xFF4A6572),
                            unfocusedIndicatorColor = Color(0xFF4A6572)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (newTaskDescription.isNotBlank()) {
                                viewModel.addTask(newTaskDescription)
                                newTaskDescription = ""
                            }
                        },
                        modifier = Modifier.size(50.dp),
                        containerColor = Color(0xFF4A6572)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
                    }
                }
            }
        }

        // DELETE ALL BUTTON - EN ESPAÑOL
        if (tasks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { viewModel.deleteAllTasks() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53E3E),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar todo")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar Todas las Tareas") // EN ESPAÑOL
            }
        }

        // DIÁLOGO DE EDICIÓN - EN ESPAÑOL
        EditTaskDialog(viewModel = viewModel)
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFA0A0A0),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ModernTaskItem(
    task: com.example.lab_08.data.Task,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit, // AGREGADO PARÁMETRO EDITAR
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D3748)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox circular
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (task.isCompleted) Color(0xFF4A6572)
                        else Color(0xFF4A6572).copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Text(
                        "✓",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Task content - clickable para marcar como completada
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onToggleComplete() }
            ) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted) {
                        Color(0xFFA0A0A0)
                    } else {
                        Color.White
                    }
                )
                if (task.isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Completada", // EN ESPAÑOL
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4A6572),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botón editar - NUEVO
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar tarea",
                    tint = Color(0xFF4A6572)
                )
            }

            // Botón eliminar
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFE53E3E)
                )
            }
        }
    }
}

// DIÁLOGO DE EDICIÓN - EN ESPAÑOL
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
            title = { Text("Editar Tarea") }, // EN ESPAÑOL
            text = {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    label = { Text("Descripción de la tarea") }, // EN ESPAÑOL
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1E1E2E),
                        unfocusedContainerColor = Color(0xFF1E1E2E),
                        focusedIndicatorColor = Color(0xFF4A6572),
                        unfocusedIndicatorColor = Color(0xFF4A6572)
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.updateTask(editedText) },
                    enabled = editedText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A6572),
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar") // EN ESPAÑOL
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cancelEdit() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFA0A0A0)
                    )
                ) {
                    Text("Cancelar") // EN ESPAÑOL
                }
            },
            containerColor = Color(0xFF2D3748)
        )
    }
}