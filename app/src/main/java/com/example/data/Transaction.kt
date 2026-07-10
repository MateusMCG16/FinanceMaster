package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val description: String,
    val dateMillis: Long,
    val type: TransactionType,
    val categoryName: String
)

enum class TransactionType {
    INCOME, EXPENSE
}
