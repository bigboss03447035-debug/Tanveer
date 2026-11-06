package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DailyTask
import com.example.model.TaskData
import com.example.ui.theme.*
import com.example.ui.viewmodel.RealCashUiState
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    uiState: RealCashUiState,
    onOpenTask: (DailyTask) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = uiState.isUrdu

    // Simulated live ticker
    val tickersUrdu = listOf(
        "جاز کیش: 0301-***842 نے 600 روپے ودڈرا کیے",
        "ایزی پیسہ: 0345-***190 نے 360 روپے ودڈرا کیے",
        "راست: 0312-***774 نے 600 روپے وصول کیے",
        "جاز کیش: 0300-***441 نے 180 روپے ودڈرا کیے"
    )
    val tickersEn = listOf(
        "JazzCash: 0301-***842 withdrew Rs. 600",
        "EasyPaisa: 0345-***190 withdrew Rs. 360",
        "Raast: 0312-***774 received Rs. 600",
        "JazzCash: 0300-***441 withdrew Rs. 180"
    )
    var tickerIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            tickerIndex = (tickerIndex + 1) % tickersUrdu.size
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // 1. Top App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldContainer,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isUrdu) "ریل کیش - ڈیلی ارننگ" else "Real Cash Rewards",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isUrdu) "10 ٹاسک • 60 روپے ہر ٹاسک" else "10 Tasks • Rs. 60 Each",
                            fontSize = 11.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Language toggle chip
                FilledTonalButton(
                    onClick = onToggleLanguage,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = EmeraldContainer,
                        contentColor = EmeraldPrimaryDark
                    ),
                    modifier = Modifier.testTag("lang_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Toggle Language",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isUrdu) "English" else "اردو",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2. Live Payout Ticker
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceSubtle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF16A34A),
                        modifier = Modifier.size(8.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "تازہ ودڈرا: " else "Live Payout: ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF16A34A)
                    )
                    Text(
                        text = if (isUrdu) tickersUrdu[tickerIndex] else tickersEn[tickerIndex],
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }

        // 3. Hero Visual Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .testTag("hero_banner_card"),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_earning_banner_1793878734127),
                        contentDescription = "Real Cash Daily Rewards",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay to ensure text contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AmberGold
                        ) {
                            Text(
                                text = if (isUrdu) "روزانہ 600 روپے کمائیں" else "Earn Up to Rs. 600 Daily",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isUrdu) "10 ٹاسک مکمل کریں اور روزانہ کیش ودڈرا لیں" else "Complete 10 Daily Tasks & Withdraw Daily",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4. Wallet Balance Card (Emerald & Gold)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimaryDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card"),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = if (isUrdu) "موجودہ دستیاب بیلنس" else "Available Cash Balance",
                                fontSize = 13.sp,
                                color = EmeraldContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "Rs. ",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGoldLight
                                )
                                Text(
                                    text = "${uiState.balance.toInt()}",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToWithdraw,
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("home_withdraw_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isUrdu) "وڈرا کریں" else "Withdraw",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isUrdu) "آج کی کمائی" else "Earned Today",
                                fontSize = 11.sp,
                                color = EmeraldContainer
                            )
                            Text(
                                text = "Rs. ${uiState.earnedToday.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isUrdu) "ہر ٹاسک کی رقم" else "Per Task Reward",
                                fontSize = 11.sp,
                                color = EmeraldContainer
                            )
                            Text(
                                text = "Rs. 60 روپے",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGoldLight
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isUrdu) "روزانہ کی گنجائش" else "Daily Max Potential",
                                fontSize = 11.sp,
                                color = EmeraldContainer
                            )
                            Text(
                                text = "Rs. 600 روپے",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 5. Daily Task Progress Tracker
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_progress_card"),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        Column {
                            Text(
                                text = if (isUrdu) "آج کے ٹاسک کا ہدف" else "Today's Task Target",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isUrdu)
                                    "${uiState.completedCount} از 10 مکمل • ${uiState.remainingCount} باقی ہیں"
                                else
                                    "${uiState.completedCount} of 10 done • ${uiState.remainingCount} left",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (uiState.completedCount == 10) Color(0xFFE8F8F0) else EmeraldContainer
                        ) {
                            Text(
                                text = "${(uiState.progressFraction * 100).toInt()}%",
                                color = EmeraldPrimaryDark,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { uiState.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = EmeraldPrimary,
                        trackColor = SurfaceSubtle,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUrdu) "اگلا ریسیٹ: رات 12 بجے" else "Resets at midnight",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        TextButton(
                            onClick = onNavigateToTasks,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (isUrdu) "تمام 10 ٹاسک دیکھیں >" else "View all 10 tasks >",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            }
        }

        // 6. Section Header: Recommended Next Tasks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isUrdu) "آج کے ٹاسک (60 روپے فی ٹاسک)" else "Today's Tasks (Rs. 60 each)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onNavigateToTasks) {
                    Text(if (isUrdu) "سب دیکھیں" else "See All", color = EmeraldPrimary, fontSize = 12.sp)
                }
            }
        }

        // Top 3 next tasks previews
        val previewTasks = TaskData.ALL_DAILY_TASKS.take(4)
        items(previewTasks) { task ->
            val isDone = uiState.completedTaskIds.contains(task.id)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isDone) { onOpenTask(task) }
                    .testTag("home_task_card_${task.id}"),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDone) SurfaceSubtle else EmeraldContainer,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else task.icon,
                                    contentDescription = null,
                                    tint = if (isDone) Color(0xFF16A34A) else EmeraldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isUrdu) task.titleUrdu else task.titleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isUrdu) task.descriptionUrdu else task.descriptionEn,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isDone) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F8F0)
                        ) {
                            Text(
                                text = if (isUrdu) "مکمل ✔" else "Done ✔",
                                color = Color(0xFF16A34A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { onOpenTask(task) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text(
                                text = "+Rs. 60",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
