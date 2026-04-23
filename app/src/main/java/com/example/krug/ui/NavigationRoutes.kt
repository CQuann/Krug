package com.example.krug.ui

sealed class Screen(val route: String) {
    object LoginEmail : Screen("login_email")

    object VerifyCode : Screen("verify_code/{email}") {
        fun passArgs(email: String): String = "verify_code/$email"
    }

    object RegisterProfile : Screen("register_profile/{email}") {
        fun passArgs(email: String): String = "register_profile/$email"
    }

    object MainApp : Screen("main_app")
    object Splash : Screen("splash")

    object EditProfile : Screen("edit_profile")
}

