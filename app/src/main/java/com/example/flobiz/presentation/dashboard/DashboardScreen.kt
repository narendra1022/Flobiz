package com.example.flobiz.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flobiz.R

@Composable
fun DashboardScreen(
    viewModel: DashBoardViewModel = hiltViewModel(),
    onAddTransaction: () -> Unit,
    onTransactionClick: (String) -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val filterQuery by viewModel.filterQuery.collectAsState()

    // Clearing filter when the screen is recomposed
    LaunchedEffect(Unit) {
        viewModel.updateFilterQuery("")
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = colorResource(id = R.color.colorPrimary),
                contentColor = Color.White,
                modifier = Modifier.height(45.dp),
                onClick = onAddTransaction,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add Task")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            OutlinedTextField(
                value = filterQuery,
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                onValueChange = { query ->
                    viewModel.updateFilterQuery(query)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                placeholder = {
                    Text(
                        text = "Search",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                singleLine = true
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                text = "Recent Transactions",
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
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
