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
    object AvatarUpload : Screen("avatar_upload")
    object CreateEvent : Screen("create_event")
    object EventAvatarUpload : Screen("event_avatar_upload/{eventId}") {
        fun passArgs(eventId: String) = "event_avatar_upload/$eventId"
    }

    object EventScreen : Screen("event_screen/{eventId}") {
        fun passArgs(eventId: String) = "event_screen/$eventId"
    }

    object EventDetail : Screen("event_detail/{eventId}") {
        fun passArgs(eventId: String) = "event_detail/$eventId"
    }

    object EditProfile : Screen("edit_profile")
    object EditEvent : Screen("edit_event/{eventId}") {
        fun passArgs(eventId: String) = "edit_event/$eventId"
    }
}

