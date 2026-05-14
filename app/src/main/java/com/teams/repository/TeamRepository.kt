package com.teams.repository

import android.content.Context
import com.teams.model.TeamModel

interface TeamRepository {
    suspend fun getTeams(context: Context): Result<List<TeamModel>>
}