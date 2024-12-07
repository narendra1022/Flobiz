package com.example.flobiz.presentation.transactionEntryScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.flobiz.R
import com.example.flobiz.data.model.Transaction
import com.example.flobiz.data.model.TransactionType
import com.example.flobiz.presentation.dashboard.DashBoardViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Record Expense",
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(fontSize = 20.sp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onTransactionSaved
                    ) {
                        Icon(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(7.dp),
                            painter = painterResource(id = R.drawable.arrow_left),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

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
                        scope.launch {
                            snackbarHostState.showSnackbar("Add amount")
                            // showSnackbar - suspend function
                        }
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
