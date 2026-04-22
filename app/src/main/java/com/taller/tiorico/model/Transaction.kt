package com.taller.tiorico.model

import java.util.UUID

enum class TransactionType {
    SAVE, INVEST, SPEND, EVENT
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType = TransactionType.SAVE,
    val amount: Double = 0.0,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
