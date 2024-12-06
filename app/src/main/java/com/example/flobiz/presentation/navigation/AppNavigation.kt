package com.example.flobiz.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.flobiz.presentation.authentication.AuthScreen
import com.example.flobiz.presentation.authentication.AuthViewModel
import com.example.flobiz.presentation.screens.DashboardScreen

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
            DashboardScreen()
        }

    }
}
