package com.teams.domain.usecase

import android.content.Context
import com.teams.data.model.TeamModel
import com.teams.domain.repository.TeamRepository

class TeamUseCase(private val repository: TeamRepository) {
    suspend fun getTeams(context: Context): Result<List<TeamModel>> {
        return repository.getTeams(context)
    }
}
