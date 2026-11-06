package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.model.PaymentMethodInfo
import com.example.model.PaymentMethods
import com.example.ui.theme.*
import com.example.ui.viewmodel.RealCashUiState

@Composable
fun WithdrawScreen(
    uiState: RealCashUiState,
    transactions: List<TransactionEntity>,
    onRequestWithdrawal: (Double, String, String, String) -> Unit,
    onViewReceipt: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = uiState.isUrdu

    var selectedMethod by remember { mutableStateOf(PaymentMethods.JAZZ_CASH) }
    var accountTitle by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf(if (uiState.balance >= 60) "60" else "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // 1. Balance Header
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimaryDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isUrdu) "ودڈرا کے لیے دستیاب رقم" else "Withdrawable Balance",
                                fontSize = 13.sp,
                                color = EmeraldContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rs. ${uiState.balance.toInt()}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AmberGold
                        ) {
                            Text(
                                text = if (isUrdu) "ڈیلی ودڈرا فعال" else "Daily Payout Active",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isUrdu)
                            "کم از کم ودڈرا صرف 60 روپے ہے۔ 1 ٹاسک مکمل کر کے بھی رقم نکال سکتے ہیں!"
                        else
                            "Minimum withdrawal is only Rs. 60. You can withdraw after completing just 1 task!",
                        fontSize = 11.sp,
                        color = EmeraldContainer
                    )
                }
            }
        }

        // 2. Select Payment Method
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isUrdu) "1. ودڈرا کا طریقہ کار منتخب کریں:" else "1. Select Payment Method:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethods.ALL_METHODS.take(2).forEach { method ->
                        val isSelected = selectedMethod.id == method.id
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) method.badgeBg else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(method.brandColor)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethod = method }
                                .testTag("select_method_${method.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = method.brandColor,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (method.id == "JAZZCASH") "JazzCash" else "EasyPaisa",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) method.brandColor else TextPrimary
                                )
                                Text(
                                    text = if (method.id == "JAZZCASH") "جاز کیش" else "ایزی پیسہ",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethods.ALL_METHODS.drop(2).forEach { method ->
                        val isSelected = selectedMethod.id == method.id
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) method.badgeBg else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(method.brandColor)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethod = method }
                                .testTag("select_method_${method.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = method.brandColor,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalance,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (method.id == "RAAST") "Raast / Bank" else "SadaPay",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) method.brandColor else TextPrimary
                                )
                                Text(
                                    text = if (method.id == "RAAST") "راست اکاؤنٹ" else "سادہ پے",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Form Inputs
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isUrdu) "2. اکاؤنٹ کی تفصیلات درج کریں:" else "2. Enter Account Details:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = accountTitle,
                        onValueChange = {
                            accountTitle = it
                            errorMessage = null
                        },
                        label = { Text(if (isUrdu) "اکاؤنٹ ہولڈر کا نام" else "Account Title / Full Name") },
                        placeholder = { Text(if (isUrdu) "مثلاً محمد احمد" else "e.g. Muhammad Ahmad") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("account_title_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = {
                            accountNumber = it
                            errorMessage = null
                        },
                        label = { Text(if (isUrdu) "اکاؤنٹ یا موبائل نمبر" else "Account or Mobile Number") },
                        placeholder = { Text(if (isUrdu) selectedMethod.placeholderUrdu else selectedMethod.placeholderEn) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Dialpad, contentDescription = null, tint = EmeraldPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("account_number_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    // Quick Amount Chips
                    Column {
                        Text(
                            text = if (isUrdu) "ودڈرا رقم منتخب کریں:" else "Select Amount to Withdraw:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(60, 120, 300, 600).forEach { amt ->
                                OutlinedButton(
                                    onClick = {
                                        amountText = amt.toString()
                                        errorMessage = null
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (amountText == amt.toString()) EmeraldContainer else Color.Transparent
                                    )
                                ) {
                                    Text(
                                        text = "$amt",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (amountText == amt.toString()) EmeraldPrimaryDark else TextPrimary
                                    )
                                }
                            }

                            // Full Balance Chip
                            OutlinedButton(
                                onClick = {
                                    amountText = uiState.balance.toInt().toString()
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (amountText == uiState.balance.toInt().toString() && uiState.balance > 0) AmberContainer else Color.Transparent
                                )
                            ) {
                                Text(
                                    text = if (isUrdu) "تمام" else "All",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = AmberGold
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it
                            errorMessage = null
                        },
                        label = { Text(if (isUrdu) "رقم (روپے)" else "Amount (Rs.)") },
                        prefix = { Text("Rs. ", fontWeight = FontWeight.Bold, color = EmeraldPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("amount_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull()
                            if (amt == null || amt < 60.0) {
                                errorMessage = if (isUrdu)
                                    "کم سے کم ودڈرا رقم 60 روپے ہونی چاہیے"
                                else
                                    "Minimum withdrawal is Rs. 60"
                            } else if (amt > uiState.balance) {
                                errorMessage = if (isUrdu)
                                    "بیلنس ناکافی ہے! آپ کے پاس صرف Rs. ${uiState.balance.toInt()} موجود ہیں"
                                else
                                    "Insufficient balance! Available: Rs. ${uiState.balance.toInt()}"
                            } else if (accountTitle.isBlank()) {
                                errorMessage = if (isUrdu) "براہ کرم اکاؤنٹ کا نام درج کریں" else "Please enter account title"
                            } else if (accountNumber.isBlank() || accountNumber.length < 8) {
                                errorMessage = if (isUrdu) "براہ کرم درست اکاؤنٹ یا فون نمبر درج کریں" else "Please enter valid account/phone number"
                            } else {
                                onRequestWithdrawal(amt, selectedMethod.nameUrdu, accountTitle, accountNumber)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_withdraw_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "ودڈرا کی درخواست جمع کریں" else "Request Daily Withdrawal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // 4. Recent Withdrawals List Header
        if (transactions.isNotEmpty()) {
            item {
                Text(
                    text = if (isUrdu) "حالیہ ودڈرا ٹرانزیکشنز" else "Recent Withdrawal Requests",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(transactions.take(5).size) { index ->
                val trx = transactions[index]
                val isCompleted = trx.status == "Completed"

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewReceipt(trx) }
                        .testTag("trx_item_${trx.id}")
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
                                color = if (isCompleted) Color(0xFFE8F8F0) else Color(0xFFFFF7ED),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = if (isCompleted) Color(0xFF16A34A) else Color(0xFFEA580C),
                                        modifier = Modifier.size(22.dp)
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
                                    text = trx.trxId,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Rs. ${trx.amount.toInt()}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = EmeraldPrimaryDark
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCompleted) Color(0xFFE8F8F0) else Color(0xFFFFF7ED)
                            ) {
                                Text(
                                    text = if (isCompleted)
                                        (if (isUrdu) "منظور شدہ" else "Completed")
                                    else
                                        (if (isUrdu) "پروسیسنگ" else "Processing"),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) Color(0xFF16A34A) else Color(0xFFEA580C),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
