package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.TaskExecutionDialog
import com.example.ui.components.WithdrawalReceiptDialog
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.WithdrawScreen
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RealCashViewModel

enum class MainTab {
    HOME,
    TASKS,
    WITHDRAW,
    HISTORY
}

class MainActivity : ComponentActivity() {
    private val viewModel: RealCashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RealCashApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RealCashApp(viewModel: RealCashViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val recentTasks by viewModel.recentTaskHistory.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle system back navigation
    BackHandler(enabled = currentTab != MainTab.HOME) {
        currentTab = MainTab.HOME
    }

    // Show snackbars when message arrives
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                // 1. Home
                NavigationBarItem(
                    selected = currentTab == MainTab.HOME,
                    onClick = { currentTab = MainTab.HOME },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = {
                        Text(
                            text = if (uiState.isUrdu) "ہوم" else "Home",
                            fontWeight = if (currentTab == MainTab.HOME) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimaryDark,
                        selectedTextColor = EmeraldPrimaryDark,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                // 2. 10 Daily Tasks
                NavigationBarItem(
                    selected = currentTab == MainTab.TASKS,
                    onClick = { currentTab = MainTab.TASKS },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.remainingCount > 0) {
                                    Badge(
                                        containerColor = EmeraldPrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text("${uiState.remainingCount}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.TaskAlt, contentDescription = "Tasks")
                        }
                    },
                    label = {
                        Text(
                            text = if (uiState.isUrdu) "10 ٹاسک" else "10 Tasks",
                            fontWeight = if (currentTab == MainTab.TASKS) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimaryDark,
                        selectedTextColor = EmeraldPrimaryDark,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_item_tasks")
                )

                // 3. Withdraw
                NavigationBarItem(
                    selected = currentTab == MainTab.WITHDRAW,
                    onClick = { currentTab = MainTab.WITHDRAW },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Withdraw") },
                    label = {
                        Text(
                            text = if (uiState.isUrdu) "وڈرا کیش" else "Withdraw",
                            fontWeight = if (currentTab == MainTab.WITHDRAW) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimaryDark,
                        selectedTextColor = EmeraldPrimaryDark,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_item_withdraw")
                )

                // 4. History
                NavigationBarItem(
                    selected = currentTab == MainTab.HISTORY,
                    onClick = { currentTab = MainTab.HISTORY },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "History") },
                    label = {
                        Text(
                            text = if (uiState.isUrdu) "ہسٹری" else "History",
                            fontWeight = if (currentTab == MainTab.HISTORY) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimaryDark,
                        selectedTextColor = EmeraldPrimaryDark,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_item_history")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        onOpenTask = { viewModel.openTask(it) },
                        onNavigateToTasks = { currentTab = MainTab.TASKS },
                        onNavigateToWithdraw = { currentTab = MainTab.WITHDRAW },
                        onToggleLanguage = { viewModel.toggleLanguage() }
                    )
                }

                MainTab.TASKS -> {
                    TasksScreen(
                        uiState = uiState,
                        onOpenTask = { viewModel.openTask(it) }
                    )
                }

                MainTab.WITHDRAW -> {
                    WithdrawScreen(
                        uiState = uiState,
                        transactions = transactions,
                        onRequestWithdrawal = { amount, method, title, number ->
                            viewModel.requestWithdrawal(amount, method, title, number)
                        },
                        onViewReceipt = { viewModel.showReceipt(it) }
                    )
                }

                MainTab.HISTORY -> {
                    HistoryScreen(
                        uiState = uiState,
                        transactions = transactions,
                        taskRecords = recentTasks,
                        onViewReceipt = { viewModel.showReceipt(it) },
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onResetTasksDebug = { viewModel.resetDailyTasksDebug() }
                    )
                }
            }
        }
    }

    // Modal dialogs
    uiState.activeTask?.let { task ->
        TaskExecutionDialog(
            task = task,
            isUrdu = uiState.isUrdu,
            onDismiss = { viewModel.closeActiveTask() },
            onRewardEarned = { taskId ->
                viewModel.completeAndClaimTask(taskId)
            }
        )
    }

    uiState.celebrationAmount?.let { amount ->
        CelebrationDialog(
            amount = amount,
            isUrdu = uiState.isUrdu,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }

    uiState.selectedReceipt?.let { trx ->
        WithdrawalReceiptDialog(
            transaction = trx,
            isUrdu = uiState.isUrdu,
            onDismiss = { viewModel.dismissReceipt() },
            onApproveDemo = {
                viewModel.approveTransaction(trx)
            }
        )
    }
}
