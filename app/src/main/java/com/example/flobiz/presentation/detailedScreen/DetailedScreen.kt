package com.example.flobiz.presentation.detailedScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.flobiz.R
import com.example.flobiz.data.model.Transaction
import com.example.flobiz.data.model.TransactionType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    transactionId: String,
    viewModel: DetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onTransactionDeleted: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var isDatePickerVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Fetching the transaction details
    val transaction by remember(transactionId) {
        viewModel.getTransactionById(transactionId)
    }.collectAsState(initial = null)

    // When transaction is fetched, populating the fields with existing data
    LaunchedEffect(transaction) {
        transaction?.let {
            amount = it.amount.toString()
            description = it.description
            transactionType = it.transactionType
            selectedDate = it.date
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Expense",
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(fontSize = 20.sp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(7.dp),
                            painter = painterResource(id = R.drawable.arrow_left),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Delete transaction
                            viewModel.viewModelScope.launch {
                                viewModel.deleteTransaction(transactionId)
                                onTransactionDeleted()
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(7.dp),
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete"
                        )
                    }
                }
            )
        }
    ) { padding ->
        transaction?.let { txn ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Transaction Type Toggle
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(
                        selected = transactionType == TransactionType.EXPENSE,
                        onClick = { transactionType = TransactionType.EXPENSE }
                    )
                    Text("Expense", modifier = Modifier.align(Alignment.CenterVertically))

                    RadioButton(
                        selected = transactionType == TransactionType.INCOME,
                        onClick = { transactionType = TransactionType.INCOME }
                    )
                    Text("Income", modifier = Modifier.align(Alignment.CenterVertically))
                }

                // Date Selection
                OutlinedTextField(
                    value = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(selectedDate),
                    onValueChange = {},
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { isDatePickerVisible = true }) {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(7.dp),
                                painter = painterResource(id = R.drawable.calendar),
                                contentDescription = "Change Date"
                            )
                        }
                    }
                )

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Amount",
                        modifier = Modifier.weight(2.5f)
                    )
                    Text(
                        text = "₹",
                        modifier = Modifier.padding(end = 8.dp)
                    )
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.colorPrimary),
                        contentColor = Color.White
                    ),
                    onClick = {
                        if (amount.isNotEmpty() && amount.toDoubleOrNull() != null) {
                            val updatedTransaction = Transaction(
                                id = txn.id,
                                date = selectedDate,
                                transactionType = transactionType,
                                amount = amount.toDouble(),
                                description = description
                            )
                            scope.launch {
                                viewModel.updateTransaction(updatedTransaction)
                                onBack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Transaction", color = Color.White)
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
        } ?: run {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }
    }
}

