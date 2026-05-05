package com.example.krug.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.SessionManager
import com.example.krug.data.model.DataResult
import com.example.krug.data.model.UserData

import com.example.krug.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    fun loadUser(){
        viewModelScope.launch {
            val token = sessionManager.getToken() ?: return@launch

            when (val result = authRepository.getUserData()){
                is DataResult.Success -> {
                    _userData.value = result.data
                }
                is DataResult.Error -> {

                }
            }
        }
    }


    fun changeUserData(
        username: String,
        email: String,
        displayName: String,
        birthday: String
    ) {
        viewModelScope.launch {
            val token = sessionManager.getToken() ?: return@launch

            val result = authRepository.editUserData(
                userData = UserData(
                    email = email,
                    display_name = displayName,
                    birthday = birthday,
                    username = username
                )
            )

            when (result) {
                is DataResult.Success -> {
                    loadUser()
                }
                is DataResult.Error -> {

                }
            }
        }
    }
}