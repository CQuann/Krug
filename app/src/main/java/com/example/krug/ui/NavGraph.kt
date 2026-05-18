package com.example.krug.ui

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.krug.data.model.RequestState
import com.example.krug.ui.screens.auth.AvatarUploadScreen
import com.example.krug.ui.screens.auth.AvatarUploadViewModel
import com.example.krug.ui.screens.main.MainAppScreen
import com.example.krug.ui.screens.auth.LoginEmailScreen
import com.example.krug.ui.screens.auth.LoginEmailViewModel
import com.example.krug.ui.screens.auth.RegisterNavigation
import com.example.krug.ui.screens.auth.RegisterProfileScreen
import com.example.krug.ui.screens.auth.RegisterProfileViewModel
import com.example.krug.ui.screens.auth.VerifyCodeScreen
import com.example.krug.ui.screens.auth.VerifyCodeViewModel
import com.example.krug.ui.screens.auth.VerifyNavigation
import com.example.krug.ui.screens.event.eventDetail.EventDetailScreen
import com.example.krug.ui.screens.event.eventDetail.EventDetailViewModel
import com.example.krug.ui.screens.event.EventScreen
import com.example.krug.ui.screens.event.EventViewModel
import com.example.krug.ui.screens.event.createEvent.CreateEventNavigation
import com.example.krug.ui.screens.event.createEvent.CreateEventScreen
import com.example.krug.ui.screens.event.createEvent.CreateEventUiState
import com.example.krug.ui.screens.event.createEvent.CreateEventViewModel
import com.example.krug.ui.screens.event.createEvent.EventAvatarUploadScreen
import com.example.krug.ui.screens.event.createEvent.EventAvatarUploadViewModel
import com.example.krug.ui.screens.event.createEvent.EventFormData
import com.example.krug.ui.screens.event.editEvent.EditEventNavigation
import com.example.krug.ui.screens.event.editEvent.EditEventViewModel
import com.example.krug.ui.screens.event.eventDetail.DetailNavigationEvent
import com.example.krug.ui.screens.main.EditProfile
import com.example.krug.ui.screens.main.EditProfileViewModel
import com.example.krug.ui.screens.main.MainAppViewModel
import com.example.krug.ui.screens.splash.SplashNavigation
import com.example.krug.ui.screens.splash.SplashScreen
import com.example.krug.ui.screens.splash.SplashViewModel

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Splash.route) {

        // Splash
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

        // LoginEmail
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

        // VerifyCode
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

        // Registration
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

        // Uploading user's avatar
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

        // Create event screen
        composable(Screen.CreateEvent.route) {
            val viewModel: CreateEventViewModel = hiltViewModel()
            val formData by viewModel.formData.collectAsStateWithLifecycle()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val titleError by viewModel.titleError.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect { nav ->
                    when (nav) {
                        is CreateEventNavigation.GoToEventAvatarUpload ->
                            navController.navigate(Screen.EventAvatarUpload.passArgs(nav.eventId))
                    }
                }
            }

            CreateEventScreen(
                formData = formData,
                uiState = uiState,
                titleError = titleError,
                onTitleChange = viewModel::updateTitle,
                onDescriptionChange = viewModel::updateDescription,
                onLocationChange = viewModel::updateLocation,
                onStartDateChange = viewModel::updateStartDate,
                onStartTimeChange = viewModel::updateStartTime,
                onEndDateChange = viewModel::updateEndDate,
                onEndTimeChange = viewModel::updateEndTime,
                onColorChange = viewModel::updateColor,
                onSaveClick = viewModel::createEvent
            )
        }

        // Uploading event's avatar
        composable(
            route = Screen.EventAvatarUpload.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: EventAvatarUploadViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val avatarUri by viewModel.avatarUri.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect {
                    navController.navigate(Screen.MainApp.route) {
                        popUpTo(Screen.MainApp.route) { inclusive = true }
                    }
                }
            }

            EventAvatarUploadScreen(
                uiState = uiState,
                avatarUri = avatarUri,
                onSetAvatarUri = viewModel::setAvatarUri,
                onUploadAvatar = viewModel::uploadAvatar,
                onSkip = viewModel::skip
            )
        }

        // Main screen
        composable(Screen.MainApp.route) {
            val viewModel: MainAppViewModel = hiltViewModel()
            val userId by viewModel.userId.collectAsStateWithLifecycle()
            val userData by viewModel.userData.collectAsStateWithLifecycle()
            val events by viewModel.events.collectAsStateWithLifecycle()
            val currentStatus by viewModel.currentStatus.collectAsStateWithLifecycle()
            val totalEvents by viewModel.totalEvents.collectAsStateWithLifecycle()
            val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()
            val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
            val error by viewModel.error.collectAsStateWithLifecycle()

            MainAppScreen(
                userId = userId,
                userData = userData,
                events = events,
                currentStatus = currentStatus,
                totalEvents = totalEvents,
                isLoadingMore = isLoadingMore,
                isRefreshing = isRefreshing,
                error = error,
                onEditProfileClick = {navController.navigate(Screen.EditProfile.route)},
                onStatusChange = { viewModel.onStatusChange(it) },
                onEventClick = { eventId -> navController.navigate(Screen.EventScreen.passArgs(eventId)) },
                onLoadMore = { viewModel.loadMoreEvents() },
                onCreateEventClick = { navController.navigate(Screen.CreateEvent.route) },
                onRefresh = {viewModel.onRefresh()}
            )
        }

        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: EventDetailViewModel = hiltViewModel()
            val detailedEvent by viewModel.detailedEvent.collectAsStateWithLifecycle()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val showArchiveDialog by viewModel.showArchiveDialog.collectAsStateWithLifecycle()
            val showDeleteDialog by viewModel.showDeleteDialog.collectAsStateWithLifecycle()
            val canEdit by viewModel.canEdit.collectAsStateWithLifecycle()
            val canUploadAvatar by viewModel.canUploadAvatar.collectAsStateWithLifecycle()
            val canArchive by viewModel.canArchive.collectAsStateWithLifecycle()
            val canDelete by viewModel.canDelete.collectAsStateWithLifecycle()
            val canManageMembers by viewModel.canManageMembers.collectAsStateWithLifecycle()
            val canToggleAdmin by viewModel.canToggleAdmin.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvents.collect { event ->
                    when (event) {
                        is DetailNavigationEvent.EditEvent ->
                            navController.navigate(Screen.EditEvent.passArgs(event.eventId))
                        is DetailNavigationEvent.UploadAvatar ->
                            navController.navigate(Screen.EventAvatarUpload.passArgs(event.eventId))
                        DetailNavigationEvent.GoBack -> navController.popBackStack()
                        else -> {}
                    }
                }
            }

            EventDetailScreen(
                detailedEvent = detailedEvent,
                uiState = uiState,
                showArchiveDialog = showArchiveDialog,
                showDeleteDialog = showDeleteDialog,
                canEdit = canEdit,
                canUploadAvatar = canUploadAvatar,
                canArchive = canArchive,
                canDelete = canDelete,
                canManageMembers = canManageMembers,
                canToggleAdmin = canToggleAdmin,
                currentUserId = viewModel.getCurrentUserId(),
                events = viewModel.navigationEvents,
                onBackClick = { navController.popBackStack() },
                onEditClick = { viewModel.onEditClick() },
                onUploadAvatarClick = { viewModel.onUploadAvatarClick() },
                onArchiveClick = { viewModel.onArchiveClick() },
                onDeleteClick = { viewModel.onDeleteClick() },
                onDismissArchiveDialog = { viewModel.onDismissArchiveDialog() },
                onConfirmArchive = { viewModel.onConfirmArchive() },
                onDismissDeleteDialog = { viewModel.onDismissDeleteDialog() },
                onConfirmDelete = { viewModel.onConfirmDelete() },
                onRemoveMemberClick = { userId -> viewModel.removeMember(userId) },
                onToggleAdminClick = { member -> viewModel.toggleAdmin(member) }
            )
        }

        composable(
            route = Screen.EditEvent.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: EditEventViewModel = hiltViewModel()
            val title by viewModel.title.collectAsStateWithLifecycle()
            val location by viewModel.location.collectAsStateWithLifecycle()
            val description by viewModel.description.collectAsStateWithLifecycle()
            val startDate by viewModel.startDate.collectAsStateWithLifecycle()
            val startTime by viewModel.startTime.collectAsStateWithLifecycle()
            val endDate by viewModel.endDate.collectAsStateWithLifecycle()
            val endTime by viewModel.endTime.collectAsStateWithLifecycle()
            val color by viewModel.color.collectAsStateWithLifecycle()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()
            val titleError by viewModel.titleError.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvents.collect { event ->
                    when (event) {
                        EditEventNavigation.GoBack -> navController.popBackStack()
                    }
                }
            }

            val uiStateConverted = when (val state = requestState) {
                is RequestState.Idle -> CreateEventUiState.Idle
                is RequestState.Loading -> CreateEventUiState.Loading
                is RequestState.Success -> CreateEventUiState.Idle
                is RequestState.Error -> CreateEventUiState.Error(state.message)
            }

            CreateEventScreen(
                formData = EventFormData(
                    title = title,
                    description = description,
                    location = location,
                    startDate = startDate,
                    startTime = startTime,
                    endDate = endDate,
                    endTime = endTime,
                    color = color
                ),
                uiState = uiStateConverted,
                titleError = titleError,
                onTitleChange = { viewModel.updateTitle(it) },
                onDescriptionChange = { viewModel.updateDescription(it) },
                onLocationChange = { viewModel.updateLocation(it) },
                onStartDateChange = { viewModel.updateStartDate(it) },
                onStartTimeChange = { viewModel.updateStartTime(it) },
                onEndDateChange = { viewModel.updateEndDate(it) },
                onEndTimeChange = { viewModel.updateEndTime(it) },
                onColorChange = { viewModel.updateColor(it) },
                onSaveClick = { viewModel.updateEvent() }
            )
        }
    }
}