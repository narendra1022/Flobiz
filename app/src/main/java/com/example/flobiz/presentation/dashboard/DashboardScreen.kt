package com.example.flobiz.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flobiz.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashBoardViewModel = hiltViewModel(),
    onAddTransaction: () -> Unit,
    onTransactionClick: (String) -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = colorResource(id = R.color.colorPrimary),
                contentColor = Color.White,
                modifier = Modifier
                    .height(45.dp),
                onClick = onAddTransaction,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add Task  ")
                }
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)

        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                text = "Recent Transactions",
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Box {
                TransactionList(
                    transactions = transactions,
                    onDeleteTransaction = { transaction ->
                        viewModel.deleteTransaction(transaction.id)
                    },
                    onTransactionClick = { transaction ->
                        onTransactionClick(transaction.id)
                    }
                )
            }
        }

    }
}
