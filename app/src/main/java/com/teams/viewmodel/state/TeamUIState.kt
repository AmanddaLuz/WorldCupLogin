package com.teams.viewmodel.state

import com.teams.model.TeamModel

sealed class TeamsUiState {

    data object Idle : TeamsUiState()
    data object Loading : TeamsUiState()
    data class Success(
        val teams: List<TeamModel>
    ) : TeamsUiState()
    data class Error(
        val message: String
    ) : TeamsUiState()
}