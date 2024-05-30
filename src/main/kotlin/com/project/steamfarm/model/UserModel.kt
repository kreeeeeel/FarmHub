package com.project.steamfarm.model

import java.time.LocalDateTime

data class UserModel(
    var username: String,
    var userType: UserType = UserType.WAIT_AUTH,
    var steamId: String? = null,
    var photo: String? = null,
    var password: String,
    var createdTs: Long = System.currentTimeMillis(),
    var gameStat: GameStat = GameStat()
)

data class GameStat(
    var enableDota: Boolean = true,
    var enableCs: Boolean = true,
    var currentPlayedDota: Int = 0,
    var currentDroppedCs: Boolean = false,
    var lastDropCsDate: LocalDateTime? = null
)

enum class UserType {
    WAIT_AUTH, BAD_AUTH, AUTH_COMPLETED
}