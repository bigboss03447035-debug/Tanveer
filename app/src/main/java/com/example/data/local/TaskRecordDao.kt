package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TaskRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskRecordDao {
    @Query("SELECT * FROM task_records WHERE dateKey = :dateKey")
    fun getTasksForDate(dateKey: String): Flow<List<TaskRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskRecord(record: TaskRecordEntity): Long

    @Query("SELECT taskId FROM task_records WHERE dateKey = :dateKey")
    suspend fun getCompletedTaskIdsForDate(dateKey: String): List<Int>

    @Query("SELECT SUM(rewardAmount) FROM task_records")
    fun getTotalEarned(): Flow<Double?>

    @Query("SELECT * FROM task_records ORDER BY completedTimestamp DESC LIMIT 50")
    fun getRecentTaskRecords(): Flow<List<TaskRecordEntity>>
}
