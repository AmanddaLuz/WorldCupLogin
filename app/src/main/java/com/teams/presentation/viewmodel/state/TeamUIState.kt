package com.teams.presentation.viewmodel.state

import com.teams.data.model.TeamModel

sealed class TeamUiState {

    data object Idle : TeamUiState()
    data object Loading : TeamUiState()
    data class Success(
        val teams: List<TeamModel>
    ) : TeamUiState()
    data class Error(
        val message: String
    ) : TeamUiState()
}
