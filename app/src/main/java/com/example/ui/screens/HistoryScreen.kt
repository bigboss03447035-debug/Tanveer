package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskRecordEntity
import com.example.data.model.TransactionEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.RealCashUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    uiState: RealCashUiState,
    transactions: List<TransactionEntity>,
    taskRecords: List<TaskRecordEntity>,
    onViewReceipt: (TransactionEntity) -> Unit,
    onToggleLanguage: () -> Unit,
    onResetTasksDebug: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = uiState.isUrdu
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Withdrawals, 1: Tasks Log, 2: Rules & Help

    val totalWithdrawn = remember(transactions) {
        transactions.sumOf { it.amount }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // 1. Stats Summary Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = if (isUrdu) "اکاؤنٹ کی مجموعی کارکردگی" else "Account Lifetime Overview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isUrdu) "کل ودڈرا رقم" else "Total Withdrawn",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rs. ${totalWithdrawn.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isUrdu) "آج کے ٹاسک" else "Today's Tasks",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${uiState.completedCount}/10",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isUrdu) "کل دستیاب بیلنس" else "Current Balance",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rs. ${uiState.balance.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldPrimaryDark
                            )
                        }
                    }
                }
            }
        }

        // 2. Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceSubtle,
                modifier = Modifier.fillMaxWidth(),
                indicator = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = if (isUrdu) "ودڈرا ہسٹری" else "Withdrawals",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) EmeraldPrimaryDark else TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (isUrdu) "ٹاسک آمدنی" else "Task Earnings",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) EmeraldPrimaryDark else TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = if (isUrdu) "قواعد و مدد" else "Rules & FAQ",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 2) EmeraldPrimaryDark else TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        // 3. Tab Content
        when (selectedTab) {
            0 -> {
                // Withdrawals List
                if (transactions.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isUrdu) "ابھی تک کوئی ودڈرا درخواست نہیں کی گئی" else "No withdrawal requests yet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isUrdu) "ٹاسک مکمل کریں اور 60 روپے سے ودڈرا شروع کریں!" else "Complete tasks to earn Rs. 60 and withdraw!",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                } else {
                    items(transactions) { trx ->
                        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.US)
                        val dateStr = sdf.format(Date(trx.timestamp))
                        val isDone = trx.status == "Completed"

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onViewReceipt(trx) }
                                .testTag("history_trx_${trx.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isDone) Color(0xFFE8F8F0) else Color(0xFFFFF7ED),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isDone) Icons.Default.Check else Icons.Default.Schedule,
                                                contentDescription = null,
                                                tint = if (isDone) Color(0xFF16A34A) else Color(0xFFEA580C),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = trx.paymentMethod,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "$dateStr • ${trx.accountNumber}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Rs. ${trx.amount.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = EmeraldPrimaryDark
                                    )
                                    Text(
                                        text = if (isUrdu) "رسید دیکھیں >" else "View Slip >",
                                        fontSize = 11.sp,
                                        color = EmeraldPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Task Earnings Log
                if (taskRecords.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isUrdu) "ابھی کوئی ٹاسک لاگ موجود نہیں ہے" else "No completed tasks yet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(taskRecords) { record ->
                        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.US)
                        val dateStr = sdf.format(Date(record.completedTimestamp))

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = record.taskTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(text = dateStr, fontSize = 10.sp, color = TextMuted)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldContainer
                                ) {
                                    Text(
                                        text = "+Rs. 60",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = EmeraldPrimaryDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Rules & Help FAQ
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isUrdu) "قواعد و ضوابط (App Rules):" else "App Rules & Guide:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldPrimaryDark
                            )

                            Text(
                                text = if (isUrdu)
                                    "1. روزانہ کل 10 ٹاسک دستیاب ہوتے ہیں جن میں لکی اسپن، کوئز، اسکریچ کارڈ وغیرہ شامل ہیں۔"
                                else
                                    "1. 10 Daily Tasks are available including lucky spin, quiz, scratch cards and polls.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = if (isUrdu)
                                    "2. ہر ایک ٹاسک کے مکمل ہونے پر آپ کو فوراً 60 روپے کیش دیا جاتا ہے۔"
                                else
                                    "2. Completing each task awards an instant Rs. 60 directly into your wallet balance.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = if (isUrdu)
                                    "3. تمام 10 ٹاسک مکمل کرنے پر آپ روزانہ 600 روپے تک کما سکتے ہیں۔"
                                else
                                    "3. By completing all 10 tasks, you can earn up to Rs. 600 daily.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = if (isUrdu)
                                    "4. روزانہ کا ودڈرا جاز کیش اور ایزی پیسہ پر بلا تعطل دستیاب ہے (کم سے کم رقم 60 روپے)۔"
                                else
                                    "4. Daily withdrawals via JazzCash and EasyPaisa are supported (minimum Rs. 60).",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Settings & Language switch
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = null,
                                        tint = EmeraldPrimary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isUrdu) "زبان تبدیل کریں (Language)" else "Change Language",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                }

                                Button(
                                    onClick = onToggleLanguage,
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(if (isUrdu) "English" else "اردو", fontSize = 12.sp)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Debug Reset Tasks
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.RestartAlt,
                                        contentDescription = null,
                                        tint = AmberGold
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isUrdu) "ٹاسک دوبارہ کھولیں (ٹیسٹنگ)" else "Reset Tasks (Demo)",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = onResetTasksDebug,
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(if (isUrdu) "ریسیٹ" else "Reset", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
