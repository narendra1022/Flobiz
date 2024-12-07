package com.example.flobiz.data.model

import com.google.firebase.firestore.PropertyName
import java.util.Date


data class Transaction(
    val id: String = "",
    val date: Date = Date(),
    @get:PropertyName("type")
    @set:PropertyName("type")
    var transactionType: TransactionType = TransactionType.EXPENSE,
    val amount: Double = 0.0,
    val description: String = ""
)

enum class TransactionType {
    INCOME, EXPENSE
}