package com.example.krug.ui.screens.event.eventDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.krug.R
import com.example.krug.data.model.RequestState
import com.example.krug.data.model.event.DetailedEvent
import com.example.krug.data.model.event.Event
import com.example.krug.data.model.event.Member
import com.example.krug.ui.components.DetailField
import com.example.krug.ui.theme.KrugTheme
import com.example.krug.utils.Constants
import com.example.krug.utils.DateUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    detailedEvent: DetailedEvent?,
    requestState: RequestState,
    showArchiveDialog: Boolean,
    showDeleteDialog: Boolean,
    canEdit: Boolean,
    canUploadAvatar: Boolean,
    canArchive: Boolean,
    canDelete: Boolean,
    canManageMembers: Boolean,
    canToggleAdmin: Boolean,
    currentUserId: String?,
    snackbarEvents: SharedFlow<String>,
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
    onToggleAdminClick: (Member) -> Unit,
    avatarRefreshKey: Long = 0L
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        snackbarEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
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
        when (requestState) {
            RequestState.Loading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is RequestState.Error -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Ошибка: ${requestState.message}", color = MaterialTheme.colorScheme.error) }

            RequestState.Idle, RequestState.Success -> {
                val event = detailedEvent?.event
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Аватар события
                    event?.eventId?.let {
                        val avatarUrl = "${Constants.BASE_URL}/event-avatars/$it?v=$avatarRefreshKey"
                        Box(modifier = Modifier.size(120.dp)) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                error = painterResource(R.drawable.ic_default_event_avatar)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = event?.title ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))

                    DetailField("Местоположение", event?.location, Icons.Default.LocationOn)
                    DetailField(
                        "Дата и время начала",
                        DateUtils.formatFullDateTime(event?.startDateTime),
                        Icons.Default.CalendarToday
                    )
                    DetailField(
                        "Дата и время окончания",
                        DateUtils.formatFullDateTime(event?.endDateTime),
                        Icons.Default.Schedule
                    )
                    DetailField("Описание", event?.description, Icons.Default.Description)

                    // Пригласительная ссылка с кнопкой копирования внутри поля
                    detailedEvent?.inviteLink?.let { link ->
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = link,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ссылка для приглашения") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Link,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val clipManager = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("invite link", link)
                                    clipManager.setPrimaryClip(clip)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Ссылка скопирована")
                                    }
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Скопировать")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Список участников
                    detailedEvent?.members?.let { members ->
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "Участники (${members.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        members.forEachIndexed { idx, member ->
                            key(member.userId) {
                                if (idx > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                val perms = member.permissions
                                val isCreator = perms[0] == '1'
                                val isAdmin = perms[1] == '1'
                                val isSelf = member.userId == currentUserId
                                var showMenu by remember { mutableStateOf(false) }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = "${Constants.BASE_URL}/avatars/${member.userId}",
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape),
                                        error = painterResource(R.drawable.ic_default_avatar)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        member.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    if (isCreator) {
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            Icons.Default.Star,
                                            "Создатель",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else if (isAdmin) {
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            Icons.Default.Shield,
                                            "Админ",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    if (canManageMembers && !isSelf) {
                                        Spacer(Modifier.weight(1f))
                                        Box {
                                            IconButton(onClick = { showMenu = !showMenu }) {
                                                Icon(Icons.Default.MoreVert, "Действия")
                                            }
                                            DropdownMenu(
                                                expanded = showMenu,
                                                onDismissRequest = { showMenu = false }) {
                                                DropdownMenuItem(
                                                    text = { Text("Удалить") },
                                                    onClick = {
                                                        showMenu = false; onRemoveMemberClick(
                                                        member.userId
                                                    )
                                                    }
                                                )
                                                if (canToggleAdmin) {
                                                    DropdownMenuItem(
                                                        text = { Text(if (isAdmin) "Разжаловать" else "Назначить админом") },
                                                        onClick = {
                                                            showMenu = false; onToggleAdminClick(
                                                            member
                                                        )
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Кнопка архивации
                    if (event?.status == "active" && canArchive) {
                        Spacer(Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = onArchiveClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Archive, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Архивировать событие")
                            }
                        }
                    }

                    // Кнопка удаления
                    if (canDelete) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Удалить событие")
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог архивации
    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = onDismissArchiveDialog,
            title = { Text("Архивировать событие?") },
            text = { Text("После архивации событие будет недоступно для активных действий.") },
            confirmButton = { TextButton(onClick = onConfirmArchive) { Text("Архивировать") } },
            dismissButton = { TextButton(onClick = onDismissArchiveDialog) { Text("Отмена") } }
        )
    }

    // Диалог удаления
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

// ------------ Preview ------------

@Preview(showBackground = true, name = "Detail – создатель", heightDp = 1200)
@Composable
fun EventDetailCreatorPreview() {
    KrugTheme {
        EventDetailScreen(
            detailedEvent = DetailedEvent(
                event = Event(
                    eventId = "1", title = "Пикник", location = "ЦПКиО",
                    startDateTime = "2026-05-10T15:00:00Z", endDateTime = "2026-05-10T18:00:00Z",
                    description = "Приносите еду", color = "#FF5733", status = "active"
                ),
                inviteLink = "https://krug.netlify.app/invite?token=abc123",
                members = listOf(
                    Member(
                        userId = "user1",
                        displayName = "Петр (вы)",
                        permissions = "100"
                    ), // создатель
                    Member(userId = "user2", displayName = "Иван", permissions = "010"), // админ
                    Member(
                        userId = "user3",
                        displayName = "Мария",
                        permissions = "001"
                    )  // участник
                ),
                permissions = "100"
            ),
            requestState = RequestState.Idle,
            showArchiveDialog = false,
            showDeleteDialog = false,
            canEdit = true, canUploadAvatar = true, canArchive = true, canDelete = true,
            canManageMembers = true, canToggleAdmin = true,
            currentUserId = "user1",
            snackbarEvents = MutableSharedFlow(),
            onBackClick = {}, onEditClick = {}, onUploadAvatarClick = {},
            onArchiveClick = {}, onDeleteClick = {},
            onDismissArchiveDialog = {}, onConfirmArchive = {},
            onDismissDeleteDialog = {}, onConfirmDelete = {},
            onRemoveMemberClick = {}, onToggleAdminClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail – админ (без удаления)")
@Composable
fun EventDetailAdminPreview() {
    KrugTheme {
        EventDetailScreen(
            detailedEvent = DetailedEvent(
                event = Event(
                    eventId = "2", title = "Встреча", location = "Кафе",
                    startDateTime = "2026-06-01", endDateTime = null,
                    description = null, color = "#3498DB", status = "active"
                ),
                inviteLink = null,
                members = listOf(
                    Member(
                        userId = "user1",
                        displayName = "Петр",
                        permissions = "100"
                    ), // создатель
                    Member(
                        userId = "user2",
                        displayName = "Иван (вы)",
                        permissions = "010"
                    ), // админ
                    Member(
                        userId = "user3",
                        displayName = "Мария",
                        permissions = "001"
                    )  // участник
                ),
                permissions = "010"
            ),
            requestState = RequestState.Idle,
            showArchiveDialog = false,
            showDeleteDialog = false,
            canEdit = true, canUploadAvatar = true, canArchive = true, canDelete = false,
            canManageMembers = true, canToggleAdmin = false,
            currentUserId = "user2",
            snackbarEvents = MutableSharedFlow(),
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
                    eventId = "3", title = "Семинар", location = "Офис",
                    startDateTime = "2026-07-01", endDateTime = null,
                    description = "Обязательно присутствовать", color = "#FFC300", status = "active"
                ),
                inviteLink = null,
                members = listOf(
                    Member(
                        userId = "user1",
                        displayName = "Петр",
                        permissions = "100"
                    ), // создатель
                    Member(userId = "user2", displayName = "Иван", permissions = "010"), // админ
                    Member(
                        userId = "user3",
                        displayName = "Мария (вы)",
                        permissions = "001"
                    )  // участник
                ),
                permissions = "001"
            ),
            requestState = RequestState.Idle,
            showArchiveDialog = false,
            showDeleteDialog = false,
            canEdit = false, canUploadAvatar = false, canArchive = false, canDelete = false,
            canManageMembers = false, canToggleAdmin = false,
            currentUserId = "user3",
            snackbarEvents = MutableSharedFlow(),
            onBackClick = {}, onEditClick = {}, onUploadAvatarClick = {},
            onArchiveClick = {}, onDeleteClick = {},
            onDismissArchiveDialog = {}, onConfirmArchive = {},
            onDismissDeleteDialog = {}, onConfirmDelete = {},
            onRemoveMemberClick = {}, onToggleAdminClick = {}
        )
    }
}