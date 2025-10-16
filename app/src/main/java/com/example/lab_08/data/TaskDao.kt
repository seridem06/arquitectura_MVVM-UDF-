package com.example.lab_08.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Obtener todas las tareas como Flow para observación reactiva
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<Task>>

    // Insertar una nueva tarea
    @Insert
    suspend fun insertTask(task: Task)

    // Actualizar una tarea existente
    @Update
    suspend fun updateTask(task: Task)

    // Eliminar una tarea específica
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Int)

    // Eliminar todas las tareas
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()


}