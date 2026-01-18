package com.example.vbpathshala.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Authenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    // Simulated login function
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Replace this with real auth logic
                if (email == "test@example.com" && password == "password") {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Invalid credentials")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Simulated registration function
    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Replace this with real registration logic
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
    }
}