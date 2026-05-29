package com.teams.domain.repository.response

import com.teams.data.model.TeamModel

fun TeamResponse.toDomain(): TeamModel {
    return TeamModel(
        name = name.orEmpty(),
        group = group.orEmpty()
    )
}
