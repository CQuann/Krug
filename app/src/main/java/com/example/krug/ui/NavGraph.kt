package com.example.krug.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.krug.ui.screens.main.MainAppScreen
import com.example.krug.ui.screens.auth.LoginEmailScreen
import com.example.krug.ui.screens.auth.RegisterProfileScreen
import com.example.krug.ui.screens.auth.VerifyCodeScreen
import com.example.krug.ui.screens.splash.SplashScreen


@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Splash.route) {

        composable(Screen.LoginEmail.route) {
            LoginEmailScreen(navController)
        }

        composable(
            route = Screen.VerifyCode.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyCodeScreen(navController, email)
        }

        composable(
            route = Screen.RegisterProfile.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("tempToken") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val tempToken = backStackEntry.arguments?.getString("tempToken") ?: ""
            RegisterProfileScreen(navController, email, tempToken)
        }

        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.MainApp.route) {
            MainAppScreen()
        }
    }
}