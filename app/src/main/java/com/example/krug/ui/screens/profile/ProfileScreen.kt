package com.example.krug.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.krug.R
import com.example.krug.data.model.UserData

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.krug.ui.components.AvatarPicker
import com.example.krug.ui.components.DateTimePickerField
import com.example.krug.ui.theme.KrugTheme
import java.time.LocalDate

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
){

    val userData by viewModel.userData.collectAsStateWithLifecycle()
    val isEdited by viewModel.isEdited.collectAsStateWithLifecycle()
    val usernameError by viewModel.usernameError.collectAsStateWithLifecycle()
    val avatarUri by viewModel.avatarUri.collectAsStateWithLifecycle()
    val isEditingAvatar by viewModel.isEditingAvatar.collectAsStateWithLifecycle()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileViewModel.ProfileEvent.ShowSnackbar -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is ProfileViewModel.ProfileEvent.NavigateToEmailScreen -> {
                    onLogoutClick()
                }
                else -> {}
            }
        }
    }


    ProfileScreenContent(
        userData,
        usernameError,
        checkIfEdited = viewModel::checkIfEdited,
        onBackClick = onBackClick,
        isEdited = isEdited,
        isEditingAvatar = isEditingAvatar,
        checkIfUsernameIsUnique = viewModel::checkIfUsernameIsUnique,
        editUserData = viewModel:: editUserData,
        avatarUri = avatarUri,
        setAvatarUri = viewModel:: setAvatarUri,
        changeAvatarState = viewModel:: changeAvatarState,
        logout = viewModel:: logout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent (
    userData: UserData?,
    usernameError: Boolean,
    isEdited: Boolean,
    avatarUri: Uri?,
    isEditingAvatar: Boolean?,
    checkIfEdited: (String, String, String, String) -> Unit,
    checkIfUsernameIsUnique: (String) -> Unit,
    editUserData: (String, String, String, String) -> Unit,
    onBackClick: () -> Unit,
    setAvatarUri: (Uri) -> Unit,
    changeAvatarState: () -> Unit,
    logout: () -> Unit
) {
    var username by remember (userData) { mutableStateOf(userData?.username ?: "") }
    var email by remember (userData) { mutableStateOf(userData?.email ?: "") }
    var displayName by remember (userData) { mutableStateOf(userData?.display_name ?: "") }
    var birthday by remember (userData) { mutableStateOf(userData?.birthday ?: "") }
    var birthdayDate = remember(birthday) {
        if (birthday.isNotBlank()) LocalDate.parse(birthday) else null
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "назад"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (!usernameError)
                                editUserData(username, email, displayName, birthday)
                        },
                    ) {
                        Text(
                            text = "Сохранить",
                            color = (
                                if (!isEdited || usernameError)
                                    Color(105, 105, 105, 255)
                                else
                                    MaterialTheme.colorScheme.primary
                            ),
                            fontSize = 16.sp
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            userData != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (isEditingAvatar == true) {
                        AvatarPicker(
                            currentAvatarUri = avatarUri,
                            onAvatarUriChanged = { uri -> setAvatarUri(uri) },
                            modifier = Modifier
                        )
                    } else {
                        Box(modifier = Modifier
                            .size(160.dp)
                        ) {
                            if (avatarUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(avatarUri),
                                    contentDescription = "Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .align (Alignment.Center)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_default_avatar),
                                    contentDescription = "Default avatar",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .align (Alignment.Center)
                                )
                            }
                            IconButton(
                                onClick = {
                                    changeAvatarState()
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                            OutlinedTextField(
                                onValueChange = { v ->
                                    displayName = v
                                    checkIfEdited (username, email, displayName, birthday)
                                },
                                value = displayName,
                                label = { Text("Имя") },
                                isError = false,
                                //supportingText = { displayNameError?.let { Text(it) } },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        HorizontalDivider(thickness = 1.dp)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AlternateEmail,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                            OutlinedTextField(
                                onValueChange = { v ->
                                    username = v
                                    checkIfEdited (username, email, displayName, birthday)
                                    checkIfUsernameIsUnique(username)
                                },
                                value = username,
                                label = { Text("Никнейм") },
                                isError = usernameError,
                                supportingText = { if (usernameError) Text ("Пользователь с таким никнеймом уже существует") },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        HorizontalDivider(thickness = 1.dp)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                            OutlinedTextField(
                                onValueChange = { v ->
                                    email = v
                                    checkIfEdited (username, email, displayName, birthday)
                                },
                                value = email,
                                label = { Text("Электронная почта") },
                                isError = false,
                                //supportingText = { displayNameError?.let { Text(it) } },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        HorizontalDivider(thickness = 1.dp)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cake,
                                contentDescription = "имя пользователя"
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                            DateTimePickerField(
                                label = "Дата рождения",
                                date = birthdayDate,
                                time = null,
                                onDateSelected = { date ->
                                    if (date != null) {
                                        val year = date.year
                                        val month =
                                            if (date.monthValue < 10) "0" + date.monthValue.toString()
                                            else date.monthValue
                                        val dayOfMonth =
                                            if (date.dayOfMonth < 10) "0" + date.dayOfMonth.toString()
                                            else date.dayOfMonth
                                        birthday = "$year-$month-$dayOfMonth"
                                    }
                                },
                                onTimeSelected = {},
                                enableTime = false,
                                modifier = Modifier
                            )
                        }


                    }

                    Spacer(modifier = Modifier.height(30.dp))


                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            logout()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Выйти из аккаунта",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
                }
            else -> {
                LinearProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    KrugTheme {
        ProfileScreenContent(
            userData = UserData(
                "t@gmail.com",
                "afadsna",
                "2021-10-10",
                "fjfkdfjdkfdj"
            ),
            usernameError = false,
            isEdited = true,
            checkIfEdited = { a, b, c, d -> {} },
            checkIfUsernameIsUnique = { a -> {} },
            editUserData = { a, b, c, d -> {} },
            avatarUri = null,
            onBackClick = {},
            isEditingAvatar = false,
            setAvatarUri = {},
            changeAvatarState = {}
        ) {}
    }
}