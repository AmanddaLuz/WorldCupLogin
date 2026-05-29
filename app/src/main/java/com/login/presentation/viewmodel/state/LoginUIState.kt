package com.login.presentation.viewmodel.state

sealed class LoginUIState {

    data object Idle : LoginUIState()
    data object Loading : LoginUIState()
    data class Success(val userNamer: String) : LoginUIState()
    data class Error(val message: String) : LoginUIState()
}
