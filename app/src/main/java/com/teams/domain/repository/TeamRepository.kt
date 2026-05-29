package com.teams.domain.repository

import android.content.Context
import com.teams.data.model.TeamModel

interface TeamRepository {
    suspend fun getTeams(context: Context): Result<List<TeamModel>>
}
