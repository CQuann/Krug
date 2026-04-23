package com.example.krug.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krug.data.local.TokenManager
import com.example.krug.data.model.UserData
import com.example.krug.data.model.auth.AuthResult
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
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    fun loadUser(){
        viewModelScope.launch {
            val token = tokenManager.getToken() ?: return@launch

            when (val result = authRepository.getUserData(token)){
                is AuthResult.Success -> {
                    _userData.value = result.data
                }
                is AuthResult.Error -> {

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
            val token = tokenManager.getToken() ?: return@launch

            val result = authRepository.editUserData(
                token = token,
                userData = UserData(
                    email = email,
                    display_name = displayName,
                    birthday = birthday,
                    username = username
                )
            )

            when (result) {
                is AuthResult.Success -> {
                    loadUser()
                }
                is AuthResult.Error -> {

                }
            }
        }
    }
}