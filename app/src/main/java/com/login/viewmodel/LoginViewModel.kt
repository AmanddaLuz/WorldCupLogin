package com.login.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.login.usecase.LoginUseCase
import com.login.viewmodel.state.LoginUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Idle)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun login(context: Context, user: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.Loading

            val result = withContext(Dispatchers.IO) {
                loginUseCase.login(context, user, password)
            }

            result.onSuccess { loginModel ->
                _uiState.value = LoginUIState.Success(loginModel.user.name)
            }.onFailure { exception ->
                _uiState.value = LoginUIState.Error(exception.message ?: "Erro desconhecido")
            }
        }
    }
}