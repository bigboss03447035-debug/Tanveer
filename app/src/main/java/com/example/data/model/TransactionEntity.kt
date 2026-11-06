package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trxId: String,
    val amount: Double,
    val paymentMethod: String, // JazzCash, EasyPaisa, Bank/Raast, SadaPay
    val accountTitle: String,
    val accountNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending", // Pending, Approved, Completed
    val notes: String = ""
)
