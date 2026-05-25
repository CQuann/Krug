package com.example.krug.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.krug.ui.screens.auth.AvatarUploadScreen
import com.example.krug.ui.screens.auth.AvatarUploadViewModel
import com.example.krug.ui.screens.auth.LoginEmailScreen
import com.example.krug.ui.screens.auth.LoginEmailViewModel
import com.example.krug.ui.screens.auth.RegisterProfileScreen
import com.example.krug.ui.screens.auth.RegisterProfileViewModel
import com.example.krug.ui.screens.auth.VerifyCodeScreen
import com.example.krug.ui.screens.auth.VerifyCodeViewModel
import com.example.krug.ui.screens.auth.VerifyNavigation
import com.example.krug.ui.screens.event.EventScreen
import com.example.krug.ui.screens.event.EventViewModel
import com.example.krug.ui.screens.event.createEvent.CreateEventNavigation
import com.example.krug.ui.screens.event.createEvent.CreateEventScreen
import com.example.krug.ui.screens.event.createEvent.CreateEventViewModel
import com.example.krug.ui.screens.event.createEvent.EventAvatarUploadScreen
import com.example.krug.ui.screens.event.createEvent.EventAvatarUploadViewModel
import com.example.krug.ui.screens.event.createEvent.EventFormData
import com.example.krug.ui.screens.event.editEvent.EditEventNavigation
import com.example.krug.ui.screens.event.editEvent.EditEventViewModel
import com.example.krug.ui.screens.event.eventDetail.EventDetailScreen
import com.example.krug.ui.screens.event.eventDetail.EventDetailViewModel
import com.example.krug.ui.screens.main.MainAppScreen
import com.example.krug.ui.screens.main.MainAppViewModel
import com.example.krug.ui.screens.profile.ProfileScreen
import com.example.krug.ui.screens.profile.ProfileViewModel
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
            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect { event ->
                    when (event) {
                        SplashNavigation.GoToMain -> navController.navigate(Screen.MainApp.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                        SplashNavigation.GoToLogin -> navController.navigate(Screen.LoginEmail.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            }
            SplashScreen(onCheckAuth = { viewModel.checkAuth() })
        }

        // LoginEmail
        composable(Screen.LoginEmail.route) {
            val viewModel: LoginEmailViewModel = hiltViewModel()
            val email by viewModel.email.collectAsStateWithLifecycle()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()
            val emailError by viewModel.emailError.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvent.collect { email ->
                    navController.navigate(Screen.VerifyCode.passArgs(email))
                }
            }

            LoginEmailScreen(
                email = email,
                requestState = requestState,
                emailError = emailError,
                onEmailChange = { viewModel.updateEmail(it) },
                onSendCode = { viewModel.sendCode() },
                onResetError = { viewModel.resetError() }
            )
        }

        // VerifyCode
        composable(
            route = Screen.VerifyCode.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val viewModel: VerifyCodeViewModel = hiltViewModel()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()
            val canResend by viewModel.canResend.collectAsStateWithLifecycle()
            val resendCooldown by viewModel.resendCooldown.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvents.collect { navigation ->
                    when (navigation) {
                        is VerifyNavigation.GoToMain -> navController.navigate(Screen.MainApp.route) {
                            popUpTo(Screen.LoginEmail.route) { inclusive = true }
                        }
                        is VerifyNavigation.GoToRegister -> navController.navigate(
                            Screen.RegisterProfile.passArgs(navigation.email)
                        )
                    }
                }
            }

            VerifyCodeScreen(
                email = email,
                requestState = requestState,
                canResend = canResend,
                resendCooldown = resendCooldown,
                snackbarEvents = viewModel.snackbarEvents,
                onCodeCompleted = { code -> viewModel.verifyCode(email, code) },
                onResendCode = { viewModel.resendCode(email) }
            )
        }

        // Registration
        composable(
            route = Screen.RegisterProfile.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val viewModel: RegisterProfileViewModel = hiltViewModel()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()
            val displayName by viewModel.displayName.collectAsStateWithLifecycle()
            val username by viewModel.username.collectAsStateWithLifecycle()
            val birthday by viewModel.birthday.collectAsStateWithLifecycle()
            val description by viewModel.description.collectAsStateWithLifecycle()
            val usernameAvailable by viewModel.usernameAvailable.collectAsStateWithLifecycle()
            val isCheckingUsername by viewModel.isCheckingUsername.collectAsStateWithLifecycle()
            val displayNameError by viewModel.displayNameError.collectAsStateWithLifecycle()
            val usernameError by viewModel.usernameError.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvents.collect { navigation ->
                    when (navigation) {
                        RegisterProfileViewModel.RegisterNavigation.GoToAvatarUpload ->
                            navController.navigate(Screen.AvatarUpload.route) {
                                popUpTo(Screen.RegisterProfile.route) { inclusive = true }
                            }
                    }
                }
            }

            RegisterProfileScreen(
                requestState = requestState,
                displayName = displayName,
                username = username,
                birthday = birthday,
                description = description,
                usernameAvailable = usernameAvailable,
                isCheckingUsername = isCheckingUsername,
                displayNameError = displayNameError,
                usernameError = usernameError,
                snackbarEvents = viewModel.snackbarEvents,
                onDisplayNameChange = { viewModel.updateDisplayName(it) },
                onUsernameChange = { viewModel.updateUsername(it) },
                onBirthdayChange = { viewModel.updateBirthday(it) },
                onDescriptionChange = { viewModel.updateDescription(it) },
                onRegisterClick = { viewModel.register(email) }
            )
        }

        // Uploading user's avatar
        composable(Screen.AvatarUpload.route) {
            val viewModel: AvatarUploadViewModel = hiltViewModel()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()
            val avatarUri by viewModel.avatarUri.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvents.collect {
                    navController.navigate(Screen.MainApp.route) {
                        popUpTo(Screen.LoginEmail.route) { inclusive = true }
                    }
                }
            }

            AvatarUploadScreen(
                requestState = requestState,
                avatarUri = avatarUri,
                snackbarEvents = viewModel.snackbarEvents,
                onSetAvatarUri = { viewModel.setAvatarUri(it) },
                onUploadAvatar = { viewModel.uploadAvatar() },
                onSkipAvatar = { viewModel.skipAvatar() }
            )
        }

        // Create event screen
        composable(Screen.CreateEvent.route) {
            val viewModel: CreateEventViewModel = hiltViewModel()
            val formData by viewModel.formData.collectAsStateWithLifecycle()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()
            val titleError by viewModel.titleError.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvents.collect { nav ->
                    when (nav) {
                        is CreateEventNavigation.GoToEventAvatarUpload ->
                            navController.navigate(Screen.EventAvatarUpload.passArgs(nav.eventId))
                    }
                }
            }

            CreateEventScreen(
                formData = formData,
                requestState = requestState,
                titleError = titleError,
                snackbarEvents = viewModel.snackbarEvents,
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
        ) { _ ->
            val viewModel: EventAvatarUploadViewModel = hiltViewModel()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()
            val avatarUri by viewModel.avatarUri.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.navigationEvents.collect {
                    navController.navigate(Screen.MainApp.route) {
                        popUpTo(Screen.MainApp.route) { inclusive = true }
                    }
                }
            }

            EventAvatarUploadScreen(
                requestState = requestState,
                avatarUri = avatarUri,
                snackbarEvents = viewModel.snackbarEvents,
                onSetAvatarUri = viewModel::setAvatarUri,
                onUploadAvatar = viewModel::uploadAvatar,
                onSkip = viewModel::skip
            )
        }

        // Main screen
        composable(Screen.MainApp.route) {
            val viewModel: MainAppViewModel = hiltViewModel()
            val userId by viewModel.userId.collectAsStateWithLifecycle()
            val events by viewModel.events.collectAsStateWithLifecycle()
            val currentStatus by viewModel.currentStatus.collectAsStateWithLifecycle()
            val totalEvents by viewModel.totalEvents.collectAsStateWithLifecycle()
            val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()
            val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
            val error by viewModel.error.collectAsStateWithLifecycle()
            val showJoinDialog by viewModel.showJoinDialog.collectAsStateWithLifecycle()
            val pendingJoinEvent by viewModel.pendingJoinEvent.collectAsStateWithLifecycle()

            MainAppScreen(
                userId = userId,
                events = events,
                currentStatus = currentStatus,
                totalEvents = totalEvents,
                isLoadingMore = isLoadingMore,
                isRefreshing = isRefreshing,
                error = error,
                onEditProfileClick = { navController.navigate(Screen.Profile.route) },
                onStatusChange = { viewModel.onStatusChange(it) },
                onEventClick = { eventId -> navController.navigate(Screen.EventScreen.passArgs(eventId)) },
                onLoadMore = { viewModel.loadMoreEvents() },
                onCreateEventClick = { navController.navigate(Screen.CreateEvent.route) },
                onRefresh = { viewModel.onRefresh() },
                showJoinDialog = showJoinDialog,
                pendingJoinEvent = pendingJoinEvent,
                onDismissJoinDialog = { viewModel.dismissJoinDialog() },
                onNavigateToJoinedEvent = { eventId -> navController.navigate(Screen.EventScreen.passArgs(eventId))}
            )
        }

        // Event screen (tabs: чат, планирование, альбом)
        composable(
            route = Screen.EventScreen.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { _ ->
            val viewModel: EventViewModel = hiltViewModel()
            val event by viewModel.event.collectAsStateWithLifecycle()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()

            EventScreen(
                event = event,
                requestState = requestState,
                snackbarEvents = viewModel.snackbarEvents,
                onHeaderClick = {
                    event?.let { navController.navigate(Screen.EventDetail.passArgs(it.eventId)) }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Event detail screen
        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { _ ->
            val viewModel: EventDetailViewModel = hiltViewModel()
            val detailedEvent by viewModel.detailedEvent.collectAsStateWithLifecycle()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()
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
                        is EventDetailViewModel.DetailNavigationEvent.EditEvent ->
                            navController.navigate(Screen.EditEvent.passArgs(event.eventId))
                        is EventDetailViewModel.DetailNavigationEvent.UploadAvatar ->
                            navController.navigate(Screen.EventAvatarUpload.passArgs(event.eventId))
                        EventDetailViewModel.DetailNavigationEvent.GoBack -> navController.popBackStack()
                    }
                }
            }

            EventDetailScreen(
                detailedEvent = detailedEvent,
                requestState = requestState,
                showArchiveDialog = showArchiveDialog,
                showDeleteDialog = showDeleteDialog,
                canEdit = canEdit,
                canUploadAvatar = canUploadAvatar,
                canArchive = canArchive,
                canDelete = canDelete,
                canManageMembers = canManageMembers,
                canToggleAdmin = canToggleAdmin,
                currentUserId = viewModel.getCurrentUserId(),
                snackbarEvents = viewModel.snackbarEvents,
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

        // Edit event screen
        composable(
            route = Screen.EditEvent.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { _ ->
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
                requestState = requestState,
                titleError = titleError,
                snackbarEvents = viewModel.snackbarEvents,
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

        // Profile screen
        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            val userData by viewModel.userData.collectAsStateWithLifecycle()
            val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
            val displayName by viewModel.displayName.collectAsStateWithLifecycle()
            val username by viewModel.username.collectAsStateWithLifecycle()
            val email by viewModel.email.collectAsStateWithLifecycle()
            val birthday by viewModel.birthday.collectAsStateWithLifecycle()
            val description by viewModel.description.collectAsStateWithLifecycle()
            val avatarUri by viewModel.avatarUri.collectAsStateWithLifecycle()
            val isEditingAvatar by viewModel.isEditingAvatar.collectAsStateWithLifecycle()
            val usernameAvailable by viewModel.usernameAvailable.collectAsStateWithLifecycle()
            val isCheckingUsername by viewModel.isCheckingUsername.collectAsStateWithLifecycle()
            val usernameError by viewModel.usernameError.collectAsStateWithLifecycle()
            val requestState by viewModel.requestState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.loadUser()
            }

            ProfileScreen(
                userData = userData,
                isEditing = isEditing,
                displayName = displayName,
                username = username,
                email = email,
                birthday = birthday,
                description = description,
                avatarUri = avatarUri,
                isEditingAvatar = isEditingAvatar,
                usernameAvailable = usernameAvailable,
                isCheckingUsername = isCheckingUsername,
                usernameError = usernameError,
                requestState = requestState,
                events = viewModel.events,
                onBackClick = { navController.popBackStack() },
                onEnterEditMode = { viewModel.enterEditMode() },
                onCancelEdit = { viewModel.cancelEditMode() },
                onUpdateDisplayName = { viewModel.updateDisplayName(it) },
                onUpdateUsername = { viewModel.updateUsername(it) },
                onUpdateBirthday = { viewModel.updateBirthday(it) },
                onUpdateDescription = { viewModel.updateDescription(it) },
                onStartAvatarEditing = { viewModel.startAvatarEditing() },
                onSetAvatarUri = { viewModel.setAvatarUri(it) },
                onSaveProfile = { viewModel.saveProfile() },
                onLogout = { viewModel.logout() },
                onNavigateToLogin = {
                    navController.navigate(Screen.LoginEmail.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
    }
}