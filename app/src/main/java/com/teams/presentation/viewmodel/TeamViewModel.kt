package com.teams.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teams.domain.usecase.TeamUseCase
import com.teams.presentation.viewmodel.state.TeamUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeamViewModel(private val teamUseCase: TeamUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<TeamUiState>(TeamUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun getTeams(context: Context) {
        viewModelScope.launch {
            _uiState.value = TeamUiState.Loading

            val result = withContext(Dispatchers.IO) {
                teamUseCase.getTeams(context)
            }

            result.onSuccess { loginModel ->
                _uiState.value = TeamUiState.Success(loginModel)
            }.onFailure { exception ->
                _uiState.value = TeamUiState.Error(exception.message.orEmpty())
            }
        }
    }
}
