package com.example.timetracker.repo

import android.content.Context
import com.example.timetracker.data.AppDb
import com.example.timetracker.data.Task

class TaskRepository(context: Context) {

    private val taskDao = AppDb.get(context).taskDao()

    fun observeTasks(userId: Long) =
        taskDao.observeAll(userId)

    suspend fun save(task: Task) =
        taskDao.upsert(task)

    suspend fun delete(task: Task) =
        taskDao.delete(task)

    suspend fun setCompleted(taskId: Long, done: Boolean) =
        taskDao.markCompleted(taskId, done)

    suspend fun getById(id: Long) = taskDao.getById(id) // ✅ fixed
}
