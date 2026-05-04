package com.example.krug.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.krug.ui.screens.auth.*
import com.example.krug.ui.screens.main.MainAppScreen
import com.example.krug.ui.screens.main.MainAppViewModel
import com.example.krug.ui.screens.splash.SplashNavigation
import com.example.krug.ui.screens.splash.SplashScreen
import com.example.krug.ui.screens.splash.SplashViewModel

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            val navigationEvent = viewModel.navigationEvent

            LaunchedEffect(Unit) {
                navigationEvent.collect { event ->
                    when (event) {
                        SplashNavigation.GoToMain -> {
                            navController.navigate(Screen.MainApp.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                        SplashNavigation.GoToLogin -> {
                            navController.navigate(Screen.LoginEmail.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                }
            }

            SplashScreen(
                onCheckAuth = { viewModel.checkAuth() }
            )
        }

        composable(Screen.LoginEmail.route) {
            val viewModel: LoginEmailViewModel = hiltViewModel()
            val email by viewModel.email.collectAsStateWithLifecycle()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val emailError by viewModel.emailError.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect { email ->
                    navController.navigate(Screen.VerifyCode.passArgs(email))
                }
            }

            LoginEmailScreen(
                email = email,
                uiState = uiState,
                onEmailChange = { viewModel.updateEmail(it) },
                onSendCode = { viewModel.sendCode() },
                onResetError = { viewModel.resetError() },
                emailError = emailError
            )
        }

        composable(
            route = Screen.VerifyCode.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val viewModel: VerifyCodeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect { navigation ->
                    when (navigation) {
                        is VerifyNavigation.GoToMain -> {
                            navController.navigate(Screen.MainApp.route) {
                                popUpTo(Screen.LoginEmail.route) { inclusive = true }
                            }
                        }
                        is VerifyNavigation.GoToRegister -> {
                            navController.navigate(
                                Screen.RegisterProfile.passArgs(navigation.email)
                            )
                        }
                    }
                }
            }

            VerifyCodeScreen(
                email = email,
                uiState = uiState,
                onCodeCompleted = { code -> viewModel.verifyCode(email, code) },
                onResetError = { viewModel.resetError() }
            )
        }

        composable(
            route = Screen.RegisterProfile.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val viewModel: RegisterProfileViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val displayName by viewModel.displayName.collectAsStateWithLifecycle()
            val username by viewModel.username.collectAsStateWithLifecycle()
            val birthday by viewModel.birthday.collectAsStateWithLifecycle()
            val usernameAvailable by viewModel.usernameAvailable.collectAsStateWithLifecycle()
            val isCheckingUsername by viewModel.isCheckingUsername.collectAsStateWithLifecycle()
            val displayNameError by viewModel.displayNameError.collectAsStateWithLifecycle()
            val usernameError by viewModel.usernameError.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect { navigation ->
                    when (navigation) {
                        RegisterNavigation.GoToAvatarUpload -> {
                            navController.navigate(Screen.AvatarUpload.route) {
                                popUpTo(Screen.RegisterProfile.route) { inclusive = true }
                            }
                        }
                    }
                }
            }

            RegisterProfileScreen(
                uiState = uiState,
                displayName = displayName,
                username = username,
                birthday = birthday,
                usernameAvailable = usernameAvailable,
                isCheckingUsername = isCheckingUsername,
                onDisplayNameChange = { viewModel.updateDisplayName(it) },
                onUsernameChange = { viewModel.updateUsername(it) },
                onBirthdayChange = { viewModel.updateBirthday(it) },
                onRegisterClick = { viewModel.register(email) },
                onResetError = { viewModel.resetError() },
                displayNameError = displayNameError,
                usernameError = usernameError
            )
        }

        composable(Screen.AvatarUpload.route) {
            val viewModel: AvatarUploadViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val avatarUri by viewModel.avatarUri.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect {
                    navController.navigate(Screen.MainApp.route) {
                        popUpTo(Screen.LoginEmail.route) { inclusive = true }
                    }
                }
            }

            AvatarUploadScreen(
                uiState = uiState,
                avatarUri = avatarUri,
                onSetAvatarUri = { viewModel.setAvatarUri(it) },
                onUploadAvatar = { viewModel.uploadAvatar() },
                onSkipAvatar = { viewModel.skipAvatar() }
            )
        }

        composable(Screen.MainApp.route) {
            val viewModel: MainAppViewModel = hiltViewModel()
            val userId by viewModel.userId.collectAsStateWithLifecycle()
            val userData by viewModel.userData.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
            val error by viewModel.error.collectAsStateWithLifecycle()

            MainAppScreen(
                userId = userId,
                userData = userData,
                isLoading = isLoading,
                error = error,
                onRefresh = { viewModel.loadUserData() }
            )
        }
    }
}