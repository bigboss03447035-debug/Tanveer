package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.model.DailyTask
import com.example.model.TaskData
import com.example.ui.theme.*
import com.example.ui.viewmodel.RealCashUiState

@Composable
fun TasksScreen(
    uiState: RealCashUiState,
    onOpenTask: (DailyTask) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = uiState.isUrdu
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: All, 1: Remaining, 2: Completed

    val allTasks = TaskData.ALL_DAILY_TASKS
    val filteredTasks = remember(selectedFilter, uiState.completedTaskIds) {
        when (selectedFilter) {
            1 -> allTasks.filter { !uiState.completedTaskIds.contains(it.id) }
            2 -> allTasks.filter { uiState.completedTaskIds.contains(it.id) }
            else -> allTasks
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // 1. Header Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isUrdu) "روزانہ کے 10 ٹاسک" else "Daily 10 Tasks",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isUrdu)
                                "ایک ٹاسک کے 60 روپے • تمام 10 ٹاسک کے 600 روپے"
                            else
                                "Rs. 60 per task • Complete all 10 for Rs. 600 daily",
                            fontSize = 12.sp,
                            color = EmeraldContainer
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = AmberContainer,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Rs. 60",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = OnAmberContainer
                                )
                                Text(
                                    text = if (isUrdu) "فی ٹاسک" else "Each",
                                    fontSize = 9.sp,
                                    color = OnAmberContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == 0,
                    onClick = { selectedFilter = 0 },
                    label = {
                        Text(
                            if (isUrdu) "تمام (10)" else "All (10)",
                            fontWeight = if (selectedFilter == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedFilter == 1,
                    onClick = { selectedFilter = 1 },
                    label = {
                        Text(
                            if (isUrdu) "باقی (${uiState.remainingCount})" else "Remaining (${uiState.remainingCount})",
                            fontWeight = if (selectedFilter == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedFilter == 2,
                    onClick = { selectedFilter = 2 },
                    label = {
                        Text(
                            if (isUrdu) "مکمل شدہ (${uiState.completedCount})" else "Done (${uiState.completedCount})",
                            fontWeight = if (selectedFilter == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // 3. Task Cards List
        items(filteredTasks, key = { it.id }) { task ->
            val isDone = uiState.completedTaskIds.contains(task.id)

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDone) SurfaceSubtle else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDone) 1.dp else 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isDone) { onOpenTask(task) }
                    .testTag("task_item_${task.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Task Number & Icon
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDone) Color(0xFFE8F8F0) else EmeraldContainer,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(28.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = task.icon,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Title & Description
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SurfaceSubtle
                            ) {
                                Text(
                                    text = if (isUrdu) "ٹاسک ${task.id}" else "Task ${task.id}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AmberContainer
                            ) {
                                Text(
                                    text = if (isUrdu) task.categoryUrdu else task.categoryEn,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnAmberContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isUrdu) task.titleUrdu else task.titleEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDone) TextMuted else MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = if (isUrdu) task.descriptionUrdu else task.descriptionEn,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Action / Status Badge
                    if (isDone) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE8F8F0)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isUrdu) "وصول شدہ" else "Claimed",
                                    color = Color(0xFF16A34A),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = { onOpenTask(task) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = "Rs. 60",
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
