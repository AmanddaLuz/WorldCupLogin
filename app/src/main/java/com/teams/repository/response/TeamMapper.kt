package com.teams.repository.response

import com.teams.model.TeamModel

fun TeamResponse.toDomain(): TeamModel {
    return TeamModel(
        name = name.orEmpty(),
        group = group.orEmpty()
    )
}