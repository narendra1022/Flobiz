package com.example.flobiz.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flobiz.R
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
            ) {
                IconButton(
                    onClick = onTransactionSaved
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .fillMaxSize()
                            .padding(vertical = 5.dp),
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = "Back Button"
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    "Record Expense",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(fontSize = 20.sp)
                )
            }


            Row {
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        selected = transactionType == TransactionType.EXPENSE,
                        onClick = { transactionType = TransactionType.EXPENSE }
                    )
                    Text("Expense", modifier = Modifier.align(Alignment.CenterVertically))

                    RadioButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        selected = transactionType == TransactionType.INCOME,
                        onClick = { transactionType = TransactionType.INCOME }
                    )
                    Text("Income", modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            // Date Selection
            OutlinedTextField(
                value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate),
                onValueChange = {},
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { isDatePickerVisible = true }) {
                        Icon(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp),
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Change Date"
                        )
                    }
                }
            )

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Label Text on the left
                Text(
                    text = "Total Amount",
                    modifier = Modifier.weight(2.5f)
                )

                // Rupee Symbol Text in front of the TextField
                Text(
                    text = "₹",
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Input TextField on the right
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it.filter { char -> char.isDigit() || char == '.' }
                    },
                    modifier = Modifier.weight(2f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amount.isEmpty() || amount.toDoubleOrNull() == null,
                    singleLine = true
                )
            }


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
                Text(
                    "Save Transaction",
                    color = Color.White
                )
            }
        }

        // Date Picker Dialog
        if (isDatePickerVisible) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { isDatePickerVisible = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedDateMillis = datePickerState.selectedDateMillis
                            if (selectedDateMillis != null) {
                                selectedDate = Date(selectedDateMillis)
                                isDatePickerVisible = false
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isDatePickerVisible = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }

        }
    }
}
