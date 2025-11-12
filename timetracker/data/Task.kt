package com.example.timetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: Int = 0,
    val dueAt: Long? = null,
    val completed: Boolean = false,
    val userId: Long,
    val imageUri: String? = null    // ✅ NEW FIELD
)
