package com.example.flobiz.presentation.navigation

sealed class Routes(val route: String) {
    object Auth : Routes("auth")
    object Dashboard : Routes("dashboard")
    object TransactionEntry : Routes("transaction_entry")
    object TransactionDetail : Routes("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: String) = "transaction_detail/$transactionId"
    }
}