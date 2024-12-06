package com.example.flobiz.presentation.navigation

sealed class Routes(val route: String) {
    object Auth : Routes("auth")
    object Dashboard : Routes("dashboard")
    object TransactionEntry : Routes("transaction_entry")
}