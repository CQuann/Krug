package com.example.krug.ui.screens.main

import android.widget.ImageButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.krug.R
import com.example.krug.data.model.UserData
import com.example.krug.ui.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(
    userData: UserData?,
    onBackClick: () -> Unit,
    viewModel: EditProfileViewModel
){
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }

    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(userData) {
        userData?.let {
            username = it.username
            email = it.email
            displayName = it.display_name
            birthday = it.birthday ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    )
                    {
                        Icon(imageVector = Icons.Default.ArrowBack,
                            contentDescription = "назад")
                    }
                }
            )
        }
    ){ padding ->

        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(painter = painterResource(R.drawable.ex_face),
                contentDescription = "Фото профиля",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
                )
            when {
                userData != null ->{

                    Column(
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Person,
                                contentDescription = "имя пользователя")
                            Spacer(modifier = Modifier.size(65.dp))
                            if (isEditing) {
                                TextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(username)
                            }
                        }

                        HorizontalDivider(thickness = 1.dp)
                    }

                    Column(
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Email,
                                contentDescription = "email")
                            Spacer(modifier = Modifier.size(65.dp))
                            if (isEditing) {
                                TextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    enabled = false
                                )
                            } else {
                                Text(email)
                            }
                        }

                        HorizontalDivider(thickness = 1.dp)
                    }

                    Column(
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.AllInclusive,
                                contentDescription = "отображаеммое имя")
                            Spacer(modifier = Modifier.size(65.dp))
                            if (isEditing) {
                                TextField(
                                    value = displayName,
                                    onValueChange = { displayName = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(displayName)
                            }
                        }

                        HorizontalDivider(thickness = 1.dp)
                    }

                    Column(
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Cake,
                                contentDescription = "день рождения")
                            Spacer(modifier = Modifier.size(65.dp))
                            if (isEditing) {
                                TextField(
                                    value = birthday,
                                    onValueChange = { birthday = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(birthday)
                            }
                        }

                        HorizontalDivider(thickness = 1.dp)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (isEditing) {
                                viewModel.changeUserData(
                                    username = username,
                                    email = email,
                                    displayName = displayName,
                                    birthday = birthday
                                )
                                isEditing = false
                            } else {
                                isEditing = true
                            }
                        }
                    )
                    {
                        Text(if (isEditing) "Сохранить" else "Редактировать")
                    }

                }
                else -> Text("загрузка")

            }



        }
    }



}