package com.example.flobiz.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.flobiz.presentation.transactionEntryScreen.EntryScreen
import com.example.flobiz.presentation.authentication.AuthScreen
import com.example.flobiz.presentation.authentication.AuthViewModel
import com.example.flobiz.presentation.dashboard.DashboardScreen
import com.example.flobiz.presentation.detailedScreen.DetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = if (authViewModel.isUserLoggedIn())
            Routes.Dashboard.route
        else
            Routes.Auth.route
    ) {

        // Authentication Screen
        composable(Routes.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // Dashboard Screen
        composable(Routes.Dashboard.route) {
            DashboardScreen(
                onAddTransaction = {
                    navController.navigate(Routes.TransactionEntry.route)
                },
                onTransactionClick = { transactionId ->
                    navController.navigate(
                        Routes.TransactionDetail.createRoute(transactionId)
                    )
                }
            )
        }

        // Transaction Entry Screen
        composable(Routes.TransactionEntry.route) {
            EntryScreen(
                onTransactionSaved = {
                    navController.navigateUp()
                }
            )
        }

        // Transaction Detail Screen
        composable(
            route = Routes.TransactionDetail.route,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            DetailScreen(
                transactionId = transactionId ?: "",
                onBack = { navController.navigateUp() },
                onTransactionDeleted = { navController.navigateUp() }
            )
        }
    }

}

