package com.example.model

import androidx.compose.ui.graphics.Color

data class PaymentMethodInfo(
    val id: String,
    val nameUrdu: String,
    val nameEn: String,
    val placeholderUrdu: String,
    val placeholderEn: String,
    val brandColor: Color,
    val badgeBg: Color,
    val prefix: String = ""
)

object PaymentMethods {
    val JAZZ_CASH = PaymentMethodInfo(
        id = "JAZZCASH",
        nameUrdu = "جاز کیش (JazzCash)",
        nameEn = "JazzCash Mobile Account",
        placeholderUrdu = "03001234567 (11 ہندسے)",
        placeholderEn = "03001234567 (11 digits)",
        brandColor = Color(0xFFD92525),
        badgeBg = Color(0xFFFFECEC)
    )

    val EASY_PAISA = PaymentMethodInfo(
        id = "EASYPAISA",
        nameUrdu = "ایزی پیسہ (EasyPaisa)",
        nameEn = "EasyPaisa Mobile Account",
        placeholderUrdu = "03451234567 (11 ہندسے)",
        placeholderEn = "03451234567 (11 digits)",
        brandColor = Color(0xFF009F4D),
        badgeBg = Color(0xFFE8F8F0)
    )

    val RAAST_BANK = PaymentMethodInfo(
        id = "RAAST",
        nameUrdu = "راست / بینک ٹرانسفر (Raast / Bank)",
        nameEn = "Raast ID / Bank Account",
        placeholderUrdu = "راست موبائل نمبر یا IBAN نمبر درج کریں",
        placeholderEn = "Enter Raast mobile or IBAN number",
        brandColor = Color(0xFF1E40AF),
        badgeBg = Color(0xFFEFF6FF)
    )

    val SADA_PAY = PaymentMethodInfo(
        id = "SADAPAY",
        nameUrdu = "سادہ پے / نیا پے (SadaPay / NayaPay)",
        nameEn = "SadaPay / NayaPay Wallet",
        placeholderUrdu = "03001234567 موبائل نمبر",
        placeholderEn = "03001234567 Registered Number",
        brandColor = Color(0xFF0D9488),
        badgeBg = Color(0xFFF0FDFA)
    )

    val ALL_METHODS = listOf(JAZZ_CASH, EASY_PAISA, RAAST_BANK, SADA_PAY)
}
