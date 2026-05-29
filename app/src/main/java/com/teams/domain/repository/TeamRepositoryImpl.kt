package com.teams.domain.repository

import android.content.Context
import com.commons.JsonReader
import com.google.gson.Gson
import com.teams.data.model.TeamModel
import com.teams.domain.repository.response.TeamResponse
import com.teams.domain.repository.response.toDomain
import kotlinx.coroutines.delay

class TeamRepositoryImpl: TeamRepository {
    override suspend fun getTeams(context: Context): Result<List<TeamModel>> {

        delay(1000)

        return try {
            val json = JsonReader.readJsonFromAssets(context, "mock/football_teams.json")
            val teams = Gson().fromJson(json, Array<TeamResponse>::class.java)
            Result.success(teams.map { it.toDomain() })
        } catch (e: NoSuchFieldException) {
            Result.failure(e)
        }
    }
}
