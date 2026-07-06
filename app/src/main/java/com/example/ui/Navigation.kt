package com.example.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ads.AdManager
import com.example.ui.screens.*
import com.example.viewmodels.CalmViewModel

@Composable
fun CalmApp(viewModel: CalmViewModel, adManager: AdManager) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("free_play") { FreePlayScreen(navController) }
        composable("toy/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            ToyScreen(navController, index)
        }
        composable("challenges") { ChallengesScreen(navController, viewModel, adManager) }
        composable("rewards") { RewardsScreen(navController, viewModel) }
        composable("settings") { SettingsScreen(navController, viewModel) }
        composable("privacy") { PrivacyScreen(navController) }
    }
}
