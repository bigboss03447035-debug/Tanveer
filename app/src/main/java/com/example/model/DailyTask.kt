package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.ui.graphics.vector.ImageVector

enum class TaskType {
    SPIN_WHEEL,
    MATH_QUIZ,
    SCRATCH_CARD,
    CAPTCHA_SOLVER,
    DAILY_CHECKIN,
    WORD_SCRAMBLE,
    READ_ARTICLE,
    COMMUNITY_POLL,
    MEMORY_CARDS,
    VIDEO_PROMO
}

data class DailyTask(
    val id: Int,
    val type: TaskType,
    val titleUrdu: String,
    val titleEn: String,
    val descriptionUrdu: String,
    val descriptionEn: String,
    val rewardAmount: Double = 60.0,
    val icon: ImageVector,
    val categoryUrdu: String,
    val categoryEn: String,
    val estimatedSeconds: Int = 15
)

object TaskData {
    val ALL_DAILY_TASKS = listOf(
        DailyTask(
            id = 1,
            type = TaskType.SPIN_WHEEL,
            titleUrdu = "لکی اسپن وہیل",
            titleEn = "Lucky Spin Wheel",
            descriptionUrdu = "وہیل کو گھمائیں اور 60 روپے کیش حاصل کریں",
            descriptionEn = "Spin the wheel and claim your guaranteed Rs. 60 cash prize",
            rewardAmount = 60.0,
            icon = Icons.Default.Casino,
            categoryUrdu = "گیم",
            categoryEn = "Game",
            estimatedSeconds = 10
        ),
        DailyTask(
            id = 2,
            type = TaskType.MATH_QUIZ,
            titleUrdu = "ریاضی اسپیڈ کوئز",
            titleEn = "Speed Math Quiz",
            descriptionUrdu = "آسان حسابی سوال کا جواب دے کر 60 روپے کمائیں",
            descriptionEn = "Solve a simple mental math puzzle to earn Rs. 60",
            rewardAmount = 60.0,
            icon = Icons.Default.Calculate,
            categoryUrdu = "کوئز",
            categoryEn = "Quiz",
            estimatedSeconds = 15
        ),
        DailyTask(
            id = 3,
            type = TaskType.SCRATCH_CARD,
            titleUrdu = "لکی اسکریچ کارڈ",
            titleEn = "Lucky Scratch Card",
            descriptionUrdu = "کارڈ کو اسکریچ کریں اور چھپے ہوئے 60 روپے جیتیں",
            descriptionEn = "Scratch the golden card to reveal and claim Rs. 60",
            rewardAmount = 60.0,
            icon = Icons.Default.AutoAwesome,
            categoryUrdu = "انعام",
            categoryEn = "Reward",
            estimatedSeconds = 10
        ),
        DailyTask(
            id = 4,
            type = TaskType.CAPTCHA_SOLVER,
            titleUrdu = "سیکیورٹی کیپچا چیلنج",
            titleEn = "Security Captcha Solver",
            descriptionUrdu = "تصدیقی کوڈ درست درج کر کے 60 روپے شامل کریں",
            descriptionEn = "Enter the security verification code to earn Rs. 60",
            rewardAmount = 60.0,
            icon = Icons.Default.Security,
            categoryUrdu = "سیکیورٹی",
            categoryEn = "Security",
            estimatedSeconds = 15
        ),
        DailyTask(
            id = 5,
            type = TaskType.DAILY_CHECKIN,
            titleUrdu = "روزانہ حاضری بونس",
            titleEn = "Daily Check-in Streak",
            descriptionUrdu = "آج کی حاضری لگائیں اور فورا 60 روپے حاصل کریں",
            descriptionEn = "Mark today's attendance streak and claim Rs. 60 instantly",
            rewardAmount = 60.0,
            icon = Icons.Default.CalendarMonth,
            categoryUrdu = "بونس",
            categoryEn = "Bonus",
            estimatedSeconds = 5
        ),
        DailyTask(
            id = 6,
            type = TaskType.WORD_SCRAMBLE,
            titleUrdu = "لفظ پہیلی ترتیب",
            titleEn = "Word Scramble Puzzle",
            descriptionUrdu = "الٹ پلٹ حروف کو جوڑ کر لفظ بنائیں اور 60 روپے لیں",
            descriptionEn = "Unscramble the letters to reveal the word and claim Rs. 60",
            rewardAmount = 60.0,
            icon = Icons.Default.Spellcheck,
            categoryUrdu = "پہیلی",
            categoryEn = "Puzzle",
            estimatedSeconds = 20
        ),
        DailyTask(
            id = 7,
            type = TaskType.READ_ARTICLE,
            titleUrdu = "معلوماتی فنانس گائیڈ",
            titleEn = "Smart Finance Tip",
            descriptionUrdu = "بچت کا قیمتی مشورہ 10 سیکنڈ پڑھیں اور 60 روپے لیں",
            descriptionEn = "Read this 10-second smart money saving tip to claim Rs. 60",
            rewardAmount = 60.0,
            icon = Icons.Default.MenuBook,
            categoryUrdu = "آرٹیکل",
            categoryEn = "Article",
            estimatedSeconds = 15
        ),
        DailyTask(
            id = 8,
            type = TaskType.COMMUNITY_POLL,
            titleUrdu = "عوامی رائے شماری / سروے",
            titleEn = "Community Opinion Poll",
            descriptionUrdu = "اپنا پسندیدہ آپشن چنیں اور 60 روپے کا انعام پائیں",
            descriptionEn = "Cast your vote in today's opinion poll to earn Rs. 60",
            rewardAmount = 60.0,
            icon = Icons.Default.Poll,
            categoryUrdu = "سروے",
            categoryEn = "Survey",
            estimatedSeconds = 10
        ),
        DailyTask(
            id = 9,
            type = TaskType.MEMORY_CARDS,
            titleUrdu = "گولڈن کوائن میچ گیم",
            titleEn = "Coin Memory Match",
            descriptionUrdu = "دو ایک جیسے گولڈن کوائن کارڈز ملا کر 60 روپے حاصل کریں",
            descriptionEn = "Match the pair of golden coin cards to unlock Rs. 60",
            rewardAmount = 60.0,
            icon = Icons.Default.FactCheck,
            categoryUrdu = "گیم",
            categoryEn = "Game",
            estimatedSeconds = 20
        ),
        DailyTask(
            id = 10,
            type = TaskType.VIDEO_PROMO,
            titleUrdu = "سپانسرڈ پارٹنر پرومو",
            titleEn = "Sponsored Partner Promo",
            descriptionUrdu = "مختصر پریزنٹیشن دیکھیں اور آج کا آخری 60 روپے کا ٹاسک مکمل کریں",
            descriptionEn = "View the short partner highlight to unlock the final Rs. 60",
            rewardAmount = 60.0,
            icon = Icons.Default.PlayCircle,
            categoryUrdu = "ویڈیو",
            categoryEn = "Video",
            estimatedSeconds = 15
        )
    )
}
