package com.example.krug.ui.screens.event.eventDetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.event.DetailedEvent
import com.example.krug.data.model.event.Event
import com.example.krug.data.model.event.Member
import com.example.krug.ui.components.DetailField
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.AvatarUrlProvider
import com.example.krug.utils.Constants
import com.example.krug.utils.DateUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    detailedEvent: DetailedEvent?,
    uiState: EventDetailUiState,
    showArchiveDialog: Boolean,
    showDeleteDialog: Boolean,
    canEdit: Boolean,
    canUploadAvatar: Boolean,
    canArchive: Boolean,
    canDelete: Boolean,
    canManageMembers: Boolean,
    canToggleAdmin: Boolean,
    currentUserId: String?,
    events: SharedFlow<DetailNavigationEvent>,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onUploadAvatarClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissArchiveDialog: () -> Unit,
    onConfirmArchive: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDelete: () -> Unit,
    onRemoveMemberClick: (String) -> Unit,
    onToggleAdminClick: (Member) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is DetailNavigationEvent.ShowMessage -> snackbarHostState.showSnackbar(event.text)
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Детали события") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (canEdit) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                    }
                    if (canUploadAvatar) {
                        IconButton(onClick = onUploadAvatarClick) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Добавить фото")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            EventDetailUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            is EventDetailUiState.Error -> Box(
                Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center
            ) { Text("Ошибка: ${uiState.message}", color = MaterialTheme.colorScheme.error) }
            EventDetailUiState.Success -> {
                val event = detailedEvent?.event
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // аватар
                    event?.id?.let {
                        val avatarUrl = "${Constants.BASE_URL}/event-avatars/$it"
                        Box(modifier = Modifier.size(120.dp)) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                error = null
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(event?.title ?: "", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))

                    DetailField("Местоположение", event?.location, Icons.Default.LocationOn)
                    DetailField("Дата и время начала", DateUtils.formatFullDateTime(event?.startDateTime), Icons.Default.CalendarToday)
                    DetailField("Дата и время окончания", DateUtils.formatFullDateTime(event?.endDateTime), Icons.Default.Schedule)
                    DetailField("Описание", event?.description, Icons.Default.Description)

                    detailedEvent?.invite_link?.let { link ->
                        val clipboardManager = LocalClipboardManager.current
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DetailField("Ссылка для приглашения", link, Icons.Default.Link)
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(link))
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Скопировать ссылку")
                            }
                        }
                    }

                    detailedEvent?.members?.let { members ->
                        Spacer(Modifier.height(24.dp))
                        Text("Участники (${members.size})", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        members.forEachIndexed { idx, member ->
                            if (idx > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            val isAdmin = member.permissions?.let { it.length > 1 && it[1] == '1' } ?: false
                            val isCreator = member.permissions?.let { it.length > 0 && it[0] == '1' } ?: false
                            val isSelf = member.user_id == currentUserId
                            var showMenu by remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = AvatarUrlProvider.build(member.user_id),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp).clip(CircleShape),
                                    error = painterResource(R.drawable.ic_default_avatar)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(member.display_name, style = MaterialTheme.typography.bodyLarge)
                                if (isCreator) {
                                    Spacer(Modifier.width(6.dp))
                                    Icon(Icons.Default.Star, contentDescription = "Создатель", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                } else if (isAdmin) {
                                    Spacer(Modifier.width(6.dp))
                                    Icon(Icons.Default.Shield, contentDescription = "Администратор", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                }
                                if (canManageMembers && !isSelf) {
                                    Spacer(Modifier.weight(1f))
                                    Box {
                                        IconButton(onClick = { showMenu = !showMenu }) {
                                            Icon(Icons.Default.MoreVert, contentDescription = "Действия")
                                        }
                                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                            DropdownMenuItem(
                                                text = { Text("Удалить") },
                                                onClick = { showMenu = false; onRemoveMemberClick(member.user_id) }
                                            )
                                            if (canToggleAdmin) {
                                                DropdownMenuItem(
                                                    text = { Text(if (isAdmin) "Разжаловать" else "Назначить админом") },
                                                    onClick = { showMenu = false; onToggleAdminClick(member) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (event?.status == "active" && canArchive) {
                        Spacer(Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = onArchiveClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Archive, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Архивировать событие")
                        }
                    }

                    if (canDelete) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Удалить событие")
                        }
                    }
                }
            }
        }
    }

    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = onDismissArchiveDialog,
            title = { Text("Архивировать событие?") },
            text = { Text("После архивации событие будет недоступно для активных действий.") },
            confirmButton = { TextButton(onClick = onConfirmArchive) { Text("Архивировать") } },
            dismissButton = { TextButton(onClick = onDismissArchiveDialog) { Text("Отмена") } }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = onDismissDeleteDialog,
            title = { Text("Удалить событие?") },
            text = { Text("Это действие необратимо. Все данные события будут потеряны.") },
            confirmButton = { TextButton(onClick = onConfirmDelete) { Text("Удалить") } },
            dismissButton = { TextButton(onClick = onDismissDeleteDialog) { Text("Отмена") } }
        )
    }
}

@Preview(showBackground = true, name = "Detail – создатель")
@Composable
fun EventDetailCreatorPreview() {
    KrugTheme {
        EventDetailScreen(
            detailedEvent = DetailedEvent(
                event = Event(
                    id = "1", title = "Пикник", location = "ЦПКиО",
                    startDateTime = "2026-05-10T15:00:00Z", endDateTime = "2026-05-10T18:00:00Z",
                    description = "Приносите еду", color = "#FF5733", status = "active"
                ),
                invite_link = "https://krug.netlify.app/invite?token=abc123",
                members = listOf(
                    Member("user1", "Петр", permissions = "10"),
                    Member("user2", "Иван", permissions = "01"),
                    Member("user3", "Мария", permissions = "00")
                ),
                permissions = "10"
            ),
            uiState = EventDetailUiState.Success,
            showArchiveDialog = false,
            showDeleteDialog = false,
            canEdit = true, canUploadAvatar = true, canArchive = true, canDelete = true,
            canManageMembers = true, canToggleAdmin = true,
            currentUserId = "user1",
            events = MutableSharedFlow(),
            onBackClick = {}, onEditClick = {}, onUploadAvatarClick = {},
            onArchiveClick = {}, onDeleteClick = {},
            onDismissArchiveDialog = {}, onConfirmArchive = {},
            onDismissDeleteDialog = {}, onConfirmDelete = {},
            onRemoveMemberClick = {}, onToggleAdminClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail – участник")
@Composable
fun EventDetailMemberPreview() {
    KrugTheme {
        EventDetailScreen(
            detailedEvent = DetailedEvent(
                event = Event(
                    id = "2", title = "Встреча", location = "Кафе",
                    startDateTime = "2026-06-01", endDateTime = null,
                    description = null, color = "#3498DB", status = "active"
                ),
                invite_link = null,
                members = listOf(
                    Member("user2", "Иван", permissions = "00"),
                    Member("user3", "Мария", permissions = "00")
                ),
                permissions = "00"
            ),
            uiState = EventDetailUiState.Success,
            showArchiveDialog = false,
            showDeleteDialog = false,
            canEdit = false, canUploadAvatar = false, canArchive = false, canDelete = false,
            canManageMembers = false, canToggleAdmin = false,
            currentUserId = "user2",
            events = MutableSharedFlow(),
            onBackClick = {}, onEditClick = {}, onUploadAvatarClick = {},
            onArchiveClick = {}, onDeleteClick = {},
            onDismissArchiveDialog = {}, onConfirmArchive = {},
            onDismissDeleteDialog = {}, onConfirmDelete = {},
            onRemoveMemberClick = {}, onToggleAdminClick = {}
        )
    }
}