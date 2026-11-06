package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.model.TaskRecordEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class CashRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val taskDao = db.taskRecordDao()
    private val trxDao = db.transactionDao()

    private val prefs: SharedPreferences =
        context.getSharedPreferences("real_cash_prefs", Context.MODE_PRIVATE)

    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance: StateFlow<Double> = _walletBalance.asStateFlow()

    private val _completedTaskIdsToday = MutableStateFlow<Set<Int>>(emptySet())
    val completedTaskIdsToday: StateFlow<Set<Int>> = _completedTaskIdsToday.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("UR") // UR or EN
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    init {
        // Load initial balance
        val savedBalance = prefs.getFloat("wallet_balance", 0.0f).toDouble()
        _walletBalance.value = savedBalance

        val savedLang = prefs.getString("selected_lang", "UR") ?: "UR"
        _selectedLanguage.value = savedLang
    }

    fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    suspend fun refreshTodayCompletedTasks() {
        val todayKey = getTodayDateKey()
        val ids = taskDao.getCompletedTaskIdsForDate(todayKey)
        _completedTaskIdsToday.value = ids.toSet()
    }

    suspend fun completeTask(taskId: Int, taskTitle: String, rewardAmount: Double = 60.0): Boolean {
        val todayKey = getTodayDateKey()
        val currentCompleted = _completedTaskIdsToday.value

        if (currentCompleted.contains(taskId)) {
            // Already completed today
            return false
        }

        // Save record to DB
        val record = TaskRecordEntity(
            taskId = taskId,
            taskTitle = taskTitle,
            dateKey = todayKey,
            rewardAmount = rewardAmount,
            completedTimestamp = System.currentTimeMillis()
        )
        taskDao.insertTaskRecord(record)

        // Update in-memory set
        _completedTaskIdsToday.value = currentCompleted + taskId

        // Add to wallet balance
        val newBalance = _walletBalance.value + rewardAmount
        _walletBalance.value = newBalance
        prefs.edit().putFloat("wallet_balance", newBalance.toFloat()).apply()

        // Track total all-time earned
        val totalEarned = prefs.getFloat("total_earned_alltime", 0.0f) + rewardAmount.toFloat()
        prefs.edit().putFloat("total_earned_alltime", totalEarned).apply()

        return true
    }

    suspend fun requestWithdrawal(
        amount: Double,
        method: String,
        accountTitle: String,
        accountNumber: String
    ): Result<TransactionEntity> {
        if (amount < 60.0) {
            return Result.failure(Exception("کم سے کم ودڈرا رقم 60 روپے ہے / Minimum withdrawal is Rs. 60"))
        }

        if (amount > _walletBalance.value) {
            return Result.failure(Exception("آپ کے پاس ناکافی بیلنس ہے / Insufficient wallet balance"))
        }

        if (accountTitle.isBlank() || accountNumber.isBlank()) {
            return Result.failure(Exception("براہ کرم اکاؤنٹ کا نام اور نمبر درج کریں / Please provide account details"))
        }

        // Deduct balance
        val newBalance = _walletBalance.value - amount
        _walletBalance.value = newBalance
        prefs.edit().putFloat("wallet_balance", newBalance.toFloat()).apply()

        // Generate TRX ID
        val randomNum = Random.nextInt(100000, 999999)
        val trxId = "TRX-PK-$randomNum"

        val entity = TransactionEntity(
            trxId = trxId,
            amount = amount,
            paymentMethod = method,
            accountTitle = accountTitle.trim(),
            accountNumber = accountNumber.trim(),
            timestamp = System.currentTimeMillis(),
            status = "Processing",
            notes = "Daily Cash Withdrawal Request"
        )

        val rowId = trxDao.insertTransaction(entity)
        val created = entity.copy(id = rowId)

        // Track total withdrawn
        val totalWithdrawn = prefs.getFloat("total_withdrawn_alltime", 0.0f) + amount.toFloat()
        prefs.edit().putFloat("total_withdrawn_alltime", totalWithdrawn).apply()

        return Result.success(created)
    }

    suspend fun approveTransactionDemo(transaction: TransactionEntity) {
        val updated = transaction.copy(status = "Completed", notes = "فنڈز کامیابی سے منتقل ہو گئے / Funds Transferred")
        trxDao.updateTransaction(updated)
    }

    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return trxDao.getAllTransactions()
    }

    fun getRecentTasks(): Flow<List<TaskRecordEntity>> {
        return taskDao.getRecentTaskRecords()
    }

    fun getTotalWithdrawnAllTime(): Float {
        return prefs.getFloat("total_withdrawn_alltime", 0.0f)
    }

    fun getTotalEarnedAllTime(): Float {
        return prefs.getFloat("total_earned_alltime", 0.0f)
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
        prefs.edit().putString("selected_lang", lang).apply()
    }

    suspend fun resetTodayTasksForDebug() {
        _completedTaskIdsToday.value = emptySet()
    }
}
