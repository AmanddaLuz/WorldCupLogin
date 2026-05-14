package com.teams.usecase

import android.content.Context
import com.teams.model.TeamModel
import com.teams.repository.TeamRepository

class TeamUseCase(private val repository: TeamRepository) {
    suspend fun getTeams(context: Context): Result<List<TeamModel>> {
        return repository.getTeams(context)
    }
}