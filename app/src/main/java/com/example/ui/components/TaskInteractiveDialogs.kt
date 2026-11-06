package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DailyTask
import com.example.model.TaskType
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun TaskExecutionDialog(
    task: DailyTask,
    isUrdu: Boolean,
    onDismiss: () -> Unit,
    onRewardEarned: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("task_execution_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header with Task Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmberContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = OnAmberContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Rs. 60 روپے",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = OnAmberContainer
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("close_task_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isUrdu) task.titleUrdu else task.titleEn,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isUrdu) task.descriptionUrdu else task.descriptionEn,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Task-specific interactive view
                when (task.type) {
                    TaskType.SPIN_WHEEL -> SpinWheelContent(onRewardEarned = { onRewardEarned(task.id) }, isUrdu = isUrdu)
                    TaskType.MATH_QUIZ -> MathQuizContent(onRewardEarned = { onRewardEarned(task.id) }, isUrdu = isUrdu)
                    TaskType.SCRATCH_CARD -> ScratchCardContent(onRewardEarned = { onRewardEarned(task.id) }, isUrdu = isUrdu)
                    TaskType.CAPTCHA_SOLVER -> CaptchaContent(onRewardEarned = { onRewardEarned(task.id) }, isUrdu = isUrdu)
                    TaskType.DAILY_CHECKIN -> DailyCheckinContent(onRewardEarned = { onRewardEarned(task.id) }, isUrdu = isUrdu)
                    TaskType.WORD_SCRAMBLE -> WordScrambleContent(onRewardEarned = { onRewardEarned(task.id) }, isUrdu = isUrdu)
                    TaskType.READ_ARTICLE -> ReadArticleContent(onRewardEarned = { onRewardEarned(task.id) }, isUrdu = isUrdu)
                    TaskType.COMMUNITY_POLL -> CommunityPollContent(onRewardEarned = { onRewardEarned(task.id) }, isUrdu = isUrdu)
                    TaskType.MEMORY_CARDS -> MemoryCardsContent(onRewardEarned = { onRewardEarned(task.id) }, isUrdu = isUrdu)
                    TaskType.VIDEO_PROMO -> VideoPromoContent(onRewardEarned = { onRewardEarned(task.id) }, isUrdu = isUrdu)
                }
            }
        }
    }
}

// 1. Spin Wheel
@Composable
fun SpinWheelContent(onRewardEarned: () -> Unit, isUrdu: Boolean) {
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var hasWon by remember { mutableStateOf(false) }

    val colors = listOf(
        EmeraldPrimary, AmberGold, Color(0xFF2563EB), Color(0xFF7C3AED),
        EmeraldLight, AmberGoldLight, Color(0xFF0284C7), Color(0xFFE11D48)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotation.value)
            ) {
                val radius = size.minDimension / 2
                val center = Offset(size.width / 2, size.height / 2)
                val angleStep = 360f / colors.size

                for (i in colors.indices) {
                    drawArc(
                        color = colors[i],
                        startAngle = i * angleStep,
                        sweepAngle = angleStep,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )
                }

                // Outer border ring
                drawCircle(
                    color = Color.White,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 6.dp.toPx())
                )
            }

            // Center Pin / Hub
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "60 Rs",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = EmeraldPrimaryDark
                    )
                }
            }

            // Top Pointer Needle
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = AmberGoldLight,
                modifier = Modifier
                    .size(46.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-8).dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasWon) {
            Button(
                onClick = {
                    if (!isSpinning) {
                        isSpinning = true
                        scope.launch {
                            // Target angle + 5 full rotations (1800 deg)
                            val targetRotation = rotation.value + 1800f + Random.nextInt(20, 340)
                            rotation.animateTo(
                                targetValue = targetRotation,
                                animationSpec = tween(durationMillis = 3200, easing = FastOutSlowInEasing)
                            )
                            isSpinning = false
                            hasWon = true
                        }
                    }
                },
                enabled = !isSpinning,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("spin_now_button")
            ) {
                if (isSpinning) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isUrdu) "گھوم رہا ہے..." else "Spinning...")
                } else {
                    Icon(imageVector = Icons.Default.Casino, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isUrdu) "ابھی گھمائیں (اسپن کریں)" else "Spin Wheel Now", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Button(
                onClick = onRewardEarned,
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("claim_spin_reward")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "مبارک ہو! 60 روپے وصول کریں" else "Claim Rs. 60 Reward!",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 2. Math Quiz
@Composable
fun MathQuizContent(onRewardEarned: () -> Unit, isUrdu: Boolean) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }

    // Predefined simple math challenge: 45 + 15 = 60
    val question = "45 + 15 = ?"
    val options = listOf(50, 60, 65, 55)
    val correctAnswer = 60

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isUrdu) "درست جواب کا انتخاب کریں:" else "Select the correct answer:",
                    fontSize = 13.sp,
                    color = OnEmeraldContainer
                )
                Text(
                    text = question,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldPrimaryDark,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Options Grid (2x2)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (i in 0..1) {
                    val opt = options[i]
                    val isSelected = selectedOption == opt
                    OutlinedButton(
                        onClick = {
                            selectedOption = opt
                            isAnswerCorrect = (opt == correctAnswer)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) {
                                if (opt == correctAnswer) EmeraldContainer else Color(0xFFFFECEC)
                            } else Color.Transparent
                        )
                    ) {
                        Text(
                            text = "$opt",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected && opt == correctAnswer) EmeraldPrimaryDark else TextPrimary
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (i in 2..3) {
                    val opt = options[i]
                    val isSelected = selectedOption == opt
                    OutlinedButton(
                        onClick = {
                            selectedOption = opt
                            isAnswerCorrect = (opt == correctAnswer)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) {
                                if (opt == correctAnswer) EmeraldContainer else Color(0xFFFFECEC)
                            } else Color.Transparent
                        )
                    ) {
                        Text(
                            text = "$opt",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected && opt == correctAnswer) EmeraldPrimaryDark else TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isAnswerCorrect == true) {
            Button(
                onClick = onRewardEarned,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("claim_math_reward")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "شاباش! 60 روپے حاصل کریں" else "Correct! Claim Rs. 60",
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (isAnswerCorrect == false) {
            Text(
                text = if (isUrdu) "غلط جواب! دوبارہ کوشش کریں" else "Incorrect answer, try again!",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

// 3. Scratch Card
@Composable
fun ScratchCardContent(onRewardEarned: () -> Unit, isUrdu: Boolean) {
    var scratchProgress by remember { mutableStateOf(0f) }
    val isRevealed = scratchProgress >= 0.7f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, AmberGold, RoundedCornerShape(20.dp))
                .background(AmberContainer)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        if (!isRevealed) {
                            scratchProgress = (scratchProgress + (dragAmount.getDistance() / 800f)).coerceIn(0f, 1f)
                        }
                    }
                }
                .clickable {
                    // Also support click-to-scratch for accessibility
                    scratchProgress = (scratchProgress + 0.35f).coerceIn(0f, 1f)
                },
            contentAlignment = Alignment.Center
        ) {
            // Prize Layer underneath
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = AmberGold,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Rs. 60 WON!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = OnAmberContainer
                )
                Text(
                    text = if (isUrdu) "60 روپے کیش انعام!" else "Guaranteed Cash Prize",
                    fontSize = 12.sp,
                    color = OnAmberContainer
                )
            }

            // Scratch Layer overlay
            if (!isRevealed) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFCBD5E1).copy(alpha = 1f - scratchProgress),
                                    Color(0xFF94A3B8).copy(alpha = 1f - scratchProgress)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isUrdu) "انگلی سے اسکریچ کریں یا ٹیپ کریں" else "Swipe or tap to scratch",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isRevealed) {
            Button(
                onClick = onRewardEarned,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("claim_scratch_reward")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "60 روپے کیش وصول کریں" else "Claim Rs. 60 Prize",
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = if (isUrdu) "کارڈ کو پورا کھرچیں (اسکریچ کریں)" else "Scratch 70% to reveal reward",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// 4. Captcha Solver
@Composable
fun CaptchaContent(onRewardEarned: () -> Unit, isUrdu: Boolean) {
    var captchaCode by remember { mutableStateOf("7K9Q2") }
    var userInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    fun refreshCode() {
        val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        captchaCode = (1..5).map { chars[Random.nextInt(chars.length)] }.joinToString("")
        userInput = ""
        isError = false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceSubtle, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = captchaCode,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                color = EmeraldPrimaryDark
            )
            IconButton(onClick = { refreshCode() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = EmeraldPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userInput,
            onValueChange = {
                userInput = it.uppercase()
                isError = false
            },
            label = { Text(if (isUrdu) "کوڈ یہاں لکھیں" else "Type Captcha Code") },
            isError = isError,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("captcha_input"),
            shape = RoundedCornerShape(14.dp)
        )

        if (isError) {
            Text(
                text = if (isUrdu) "غلط کوڈ، دوبارہ درج کریں" else "Incorrect code, please retry",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (userInput.trim().equals(captchaCode.trim(), ignoreCase = true)) {
                    onRewardEarned()
                } else {
                    isError = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("verify_captcha_button")
        ) {
            Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isUrdu) "تصدیق کریں اور 60 روپے لیں" else "Verify & Claim Rs. 60",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 5. Daily Checkin Streak
@Composable
fun DailyCheckinContent(onRewardEarned: () -> Unit, isUrdu: Boolean) {
    val days = listOf("Day 1", "Day 2", "Day 3", "Today", "Day 5", "Day 6", "Day 7")
    val daysUrdu = listOf("دن 1", "دن 2", "دن 3", "آج", "دن 5", "دن 6", "دن 7")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0..6) {
                val isToday = (i == 3)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isToday) EmeraldContainer else SurfaceSubtle)
                        .padding(horizontal = 6.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (i <= 3) Icons.Default.CheckCircle else Icons.Default.Star,
                        contentDescription = null,
                        tint = if (isToday) EmeraldPrimary else if (i < 3) Color(0xFF16A34A) else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isUrdu) daysUrdu[i] else days[i],
                        fontSize = 10.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = if (isToday) EmeraldPrimaryDark else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRewardEarned,
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("claim_checkin_button")
        ) {
            Icon(imageVector = Icons.Default.Today, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isUrdu) "آج کی حاضری لگائیں اور 60 روپے لیں" else "Check In Today (+Rs. 60)",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 6. Word Scramble
@Composable
fun WordScrambleContent(onRewardEarned: () -> Unit, isUrdu: Boolean) {
    val targetWord = "PAISA"
    val scrambled = listOf("S", "A", "P", "I", "A")
    val selectedLetters = remember { mutableStateListOf<String>() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isUrdu) "حروف ملا کر لفظ بنائیں:" else "Arrange letters to spell 'PAISA':",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Display current built word
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .height(50.dp)
                .background(SurfaceSubtle, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val currentWord = selectedLetters.joinToString("")
            Text(
                text = if (currentWord.isEmpty()) "___" else currentWord,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                color = EmeraldPrimaryDark
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrambled Letter Chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            scrambled.forEachIndexed { index, letter ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldContainer,
                    modifier = Modifier
                        .size(46.dp)
                        .clickable {
                            if (selectedLetters.size < targetWord.length) {
                                selectedLetters.add(letter)
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = letter,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnEmeraldContainer
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = { selectedLetters.clear() }) {
            Text(if (isUrdu) "دوبارہ کوشش کریں (صاف کریں)" else "Reset letters")
        }

        Spacer(modifier = Modifier.height(8.dp))

        val isSolved = selectedLetters.joinToString("") == targetWord

        Button(
            onClick = onRewardEarned,
            enabled = isSolved,
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("claim_word_reward")
        ) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isUrdu) "60 روپے انعام حاصل کریں" else "Claim Rs. 60 Reward",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 7. Read Article
@Composable
fun ReadArticleContent(onRewardEarned: () -> Unit, isUrdu: Boolean) {
    var timeLeft by remember { mutableStateOf(8) }
    val isFinished = timeLeft <= 0

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceSubtle),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isUrdu) "روزانہ بچت کا سنہری اصول:" else "Golden Rule for Daily Savings:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EmeraldPrimaryDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isUrdu)
                        "اپنی روزانہ کی کمائی کا کم از کم 20 فیصد حصہ محفوظ رکھیں اور ایزی پیسہ یا جاز کیش اکاؤنٹ میں سیونگز بنائیں تاکہ مستقبل میں فائدہ حاصل ہو۔"
                    else
                        "Save at least 20% of your daily earnings into your mobile wallet account. Consistent small daily savings compound into financial freedom.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isFinished) {
            LinearProgressIndicator(
                progress = { (8 - timeLeft) / 8f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = EmeraldPrimary,
                trackColor = EmeraldContainer,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isUrdu) "برائے مہربانی $timeLeft سیکنڈ انتظار فرمائیں..." else "Please wait $timeLeft seconds...",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Button(
                onClick = onRewardEarned,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("claim_article_reward")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "آرٹیکل مکمل! 60 روپے لیں" else "Article Read! Claim Rs. 60",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 8. Community Poll
@Composable
fun CommunityPollContent(onRewardEarned: () -> Unit, isUrdu: Boolean) {
    var selectedPoll by remember { mutableStateOf<Int?>(null) }
    val options = listOf("EasyPaisa (ایزی پیسہ)", "JazzCash (جاز کیش)", "Raast / SadaPay (راست)")
    val percentages = listOf("45%", "42%", "13%")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isUrdu) "آپ کا پسندیدہ موبائل کیش والٹ کون سا ہے؟" else "What is your favorite mobile wallet?",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        options.forEachIndexed { index, option ->
            val isSelected = selectedPoll == index
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) EmeraldContainer else SurfaceSubtle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedPoll = index }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) EmeraldPrimaryDark else TextPrimary
                    )
                    if (selectedPoll != null) {
                        Text(
                            text = percentages[index],
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRewardEarned,
            enabled = selectedPoll != null,
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("claim_poll_reward")
        ) {
            Icon(imageVector = Icons.Default.HowToVote, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isUrdu) "ووٹ دیں اور 60 روپے حاصل کریں" else "Submit Vote & Claim Rs. 60",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 9. Memory Cards Match
@Composable
fun MemoryCardsContent(onRewardEarned: () -> Unit, isUrdu: Boolean) {
    var flippedIndices by remember { mutableStateOf(setOf<Int>()) }
    val targetPair = setOf(1, 2) // pair of gold coin positions

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isUrdu) "دو ایک جیسے گولڈن کوائنز تلاش کریں:" else "Find the matching golden coins:",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            for (i in 0..3) {
                val isFlipped = flippedIndices.contains(i)
                val isGoldCoin = (i == 1 || i == 2)

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlipped && isGoldCoin) AmberContainer else SurfaceSubtle
                    ),
                    modifier = Modifier
                        .size(62.dp)
                        .clickable {
                            flippedIndices = flippedIndices + i
                        }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (isFlipped) {
                            if (isGoldCoin) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(32.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Text("?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        val matched = flippedIndices.contains(1) && flippedIndices.contains(2)

        if (matched) {
            Button(
                onClick = onRewardEarned,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("claim_memory_reward")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "جوڑا مل گیا! 60 روپے وصول کریں" else "Pair Matched! Claim Rs. 60",
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = if (isUrdu) "کارڈز پر ٹیپ کر کے میچ کریں" else "Tap cards to flip",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// 10. Video Promo Showcase
@Composable
fun VideoPromoContent(onRewardEarned: () -> Unit, isUrdu: Boolean) {
    var promoTimer by remember { mutableStateOf(6) }
    val isComplete = promoTimer <= 0

    LaunchedEffect(Unit) {
        while (promoTimer > 0) {
            delay(1000)
            promoTimer--
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isUrdu) "سپانسرڈ ویڈیو ڈیمو فعال ہے" else "Sponsored Promo Showcase",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!isComplete) {
            LinearProgressIndicator(
                progress = { (6 - promoTimer) / 6f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = EmeraldPrimary,
                trackColor = EmeraldContainer,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isUrdu) "$promoTimer سیکنڈ باقی ہیں..." else "$promoTimer seconds remaining...",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Button(
                onClick = onRewardEarned,
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("claim_video_reward")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "آخری ٹاسک مکمل! 60 روپے لیں" else "Task Finished! Claim Rs. 60",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
