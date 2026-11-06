package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_records")
data class TaskRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Int,
    val taskTitle: String,
    val dateKey: String, // e.g. "2026-11-05"
    val rewardAmount: Double = 60.0,
    val completedTimestamp: Long = System.currentTimeMillis()
)
