package com.teams.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teams.usecase.TeamUseCase
import com.teams.viewmodel.state.TeamsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeamViewModel(private val teamUseCase: TeamUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<TeamsUiState>(TeamsUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun getTeams(context: Context) {
        viewModelScope.launch {
            _uiState.value = TeamsUiState.Loading

            val result = withContext(Dispatchers.IO) {
                teamUseCase.getTeams(context)
            }

            result.onSuccess { loginModel ->
                _uiState.value = TeamsUiState.Success(loginModel)
            }.onFailure { exception ->
                _uiState.value = TeamsUiState.Error(exception.message.orEmpty())
            }
        }
    }
}
