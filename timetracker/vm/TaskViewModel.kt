package com.example.timetracker.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetracker.data.Task
import com.example.timetracker.repo.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = TaskRepository(app)

    fun tasksFor(userId: Long) =
        repo.observeTasks(userId).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    suspend fun getById(id: Long) = repo.getById(id)   // ✅ Added

    fun toggleComplete(task: Task) = viewModelScope.launch {
        repo.setCompleted(task.id, !task.completed)
    }

    fun save(task: Task) = viewModelScope.launch { repo.save(task) }
    fun delete(task: Task) = viewModelScope.launch { repo.delete(task) }
}
