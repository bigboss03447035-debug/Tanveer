package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.TaskRecordEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.CashRepository
import com.example.model.DailyTask
import com.example.model.TaskData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RealCashUiState(
    val balance: Double = 0.0,
    val completedTaskIds: Set<Int> = emptySet(),
    val totalTasksCount: Int = 10,
    val rewardPerTask: Double = 60.0,
    val isUrdu: Boolean = true,
    val activeTask: DailyTask? = null,
    val celebrationAmount: Double? = null,
    val selectedReceipt: TransactionEntity? = null,
    val snackbarMessage: String? = null,
    val isSubmittingWithdrawal: Boolean = false
) {
    val completedCount: Int get() = completedTaskIds.size
    val remainingCount: Int get() = (totalTasksCount - completedCount).coerceAtLeast(0)
    val earnedToday: Double get() = completedCount * rewardPerTask
    val maxDailyPotential: Double get() = totalTasksCount * rewardPerTask // 10 * 60 = 600
    val progressFraction: Float get() = if (totalTasksCount > 0) completedCount.toFloat() / totalTasksCount else 0f
}

class RealCashViewModel(application: Application) : AndroidViewModel(application) {
    val repository = CashRepository(application)

    private val _uiState = MutableStateFlow(RealCashUiState())
    val uiState: StateFlow<RealCashUiState> = _uiState.asStateFlow()

    val transactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTaskHistory: StateFlow<List<TaskRecordEntity>> = repository.getRecentTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.refreshTodayCompletedTasks()
        }

        viewModelScope.launch {
            combine(
                repository.walletBalance,
                repository.completedTaskIdsToday,
                repository.selectedLanguage
            ) { balance, completed, lang ->
                _uiState.value.copy(
                    balance = balance,
                    completedTaskIds = completed,
                    isUrdu = (lang == "UR")
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun openTask(task: DailyTask) {
        _uiState.value = _uiState.value.copy(activeTask = task)
    }

    fun closeActiveTask() {
        _uiState.value = _uiState.value.copy(activeTask = null)
    }

    fun completeAndClaimTask(taskId: Int) {
        val task = TaskData.ALL_DAILY_TASKS.find { it.id == taskId } ?: return
        viewModelScope.launch {
            val success = repository.completeTask(taskId, task.titleUrdu, 60.0)
            if (success) {
                _uiState.value = _uiState.value.copy(
                    activeTask = null,
                    celebrationAmount = 60.0
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    activeTask = null,
                    snackbarMessage = if (_uiState.value.isUrdu) "یہ ٹاسک آج پہلے ہی مکمل ہو چکا ہے" else "This task has already been completed today"
                )
            }
        }
    }

    fun dismissCelebration() {
        _uiState.value = _uiState.value.copy(celebrationAmount = null)
    }

    fun showReceipt(trx: TransactionEntity) {
        _uiState.value = _uiState.value.copy(selectedReceipt = trx)
    }

    fun dismissReceipt() {
        _uiState.value = _uiState.value.copy(selectedReceipt = null)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun toggleLanguage() {
        val newLang = if (_uiState.value.isUrdu) "EN" else "UR"
        repository.setLanguage(newLang)
    }

    fun requestWithdrawal(
        amount: Double,
        method: String,
        accountTitle: String,
        accountNumber: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingWithdrawal = true)
            val result = repository.requestWithdrawal(amount, method, accountTitle, accountNumber)
            _uiState.value = _uiState.value.copy(isSubmittingWithdrawal = false)

            result.onSuccess { trx ->
                _uiState.value = _uiState.value.copy(
                    selectedReceipt = trx,
                    snackbarMessage = if (_uiState.value.isUrdu)
                        "ودڈرا درخواست موصول ہو گئی! ٹرانزیکشن پروسیس ہو رہی ہے"
                    else
                        "Withdrawal request submitted! Processing transaction"
                )

                // Simulate realistic automatic gateway processing
                launch {
                    delay(4000)
                    repository.approveTransactionDemo(trx)
                }
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = err.message ?: "ودڈرا میں خرابی / Withdrawal error"
                )
            }
        }
    }

    fun approveTransaction(trx: TransactionEntity) {
        viewModelScope.launch {
            repository.approveTransactionDemo(trx)
            _uiState.value = _uiState.value.copy(
                selectedReceipt = trx.copy(status = "Completed"),
                snackbarMessage = if (_uiState.value.isUrdu) "ٹرانزیکشن کامیابی سے منظور ہو گئی!" else "Transaction approved successfully!"
            )
        }
    }

    fun resetDailyTasksDebug() {
        viewModelScope.launch {
            repository.resetTodayTasksForDebug()
            _uiState.value = _uiState.value.copy(
                snackbarMessage = if (_uiState.value.isUrdu) "تمام 10 ٹاسک دوبارہ کھل گئے ہیں!" else "All 10 tasks reset for today!"
            )
        }
    }
}
