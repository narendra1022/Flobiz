package com.example.flobiz.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flobiz.data.model.Transaction
import com.example.flobiz.data.model.TransactionType
import com.example.flobiz.presentation.dashboard.DashBoardViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    viewModel: DashBoardViewModel = hiltViewModel(),
    onTransactionSaved: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var isDatePickerVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Type Toggle
            Text("Transaction Type", modifier = Modifier.fillMaxWidth())
            Row {
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(
                        selected = transactionType == TransactionType.EXPENSE,
                        onClick = { transactionType = TransactionType.EXPENSE }
                    )
                    Text("Expense")

                    RadioButton(
                        selected = transactionType == TransactionType.INCOME,
                        onClick = { transactionType = TransactionType.INCOME }
                    )
                    Text("Income")
                }
            }

            // Date Selection
            OutlinedTextField(
                value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate),
                onValueChange = {},
                label = { Text("Date") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Select Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { isDatePickerVisible = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Change Date")
                    }
                }
            )

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it.filter { char -> char.isDigit() || char == '.' }
                },
                label = { Text("Amount") },
                leadingIcon = { Icon(Icons.Default.AddCircle, contentDescription = "Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = amount.isEmpty() || amount.toDoubleOrNull() == null
            )

            // Description Input (Optional)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            // Save Button
            Button(
                onClick = {
                    if (amount.isNotEmpty() &&
                        amount.toDoubleOrNull() != null &&
                        amount.toDouble() > 0
                    ) {
                        val transaction = Transaction(
                            date = selectedDate,
                            transactionType = transactionType,
                            amount = amount.toDouble(),
                            description = description
                        )
                        viewModel.addTransaction(transaction)
                        onTransactionSaved()
                    } else {
                        // Show error
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Transaction")
            }
        }

        // Date Picker Dialog
        if (isDatePickerVisible) {
            DatePickerDialog(
                onDismissRequest = { isDatePickerVisible = false },
                confirmButton = {
                    TextButton(onClick = {
                        isDatePickerVisible = false
                    }) {
                        Text("OK")
                    }
                }
            ) {

            }
        }
    }
}
