package com.project.steamfarm.model

import com.project.steamfarm.data.SteamData
import java.time.LocalDateTime

data class UserModel(
    var photo: String? = null,
    var createdTs: Long = System.currentTimeMillis(),
    var gameStat: GameStat = GameStat(),
    var steam: SteamData,
)

data class GameStat(
    var enableDota: Boolean = true,
    var enableCs: Boolean = true,
    var currentPlayedDota: Int = 0,
    var currentDroppedCs: Boolean = false,
    var lastDropCsDate: LocalDateTime? = null
)