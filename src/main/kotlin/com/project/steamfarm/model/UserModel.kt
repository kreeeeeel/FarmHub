package com.project.steamfarm.model

import java.time.LocalDateTime

data class UserModel(
    var username: String,
    var userType: UserType = UserType.WAIT_AUTH,
    var steamId: String? = null,
    var photo: String? = null,
    var password: String,
    var time: Long = System.currentTimeMillis(),
    var gameStat: GameStat = GameStat()
)

data class GameStat(
    var farmDota: Boolean = false,
    var farmCs: Boolean = false,
    var dotaHour: Int? = null,
    var csDropped: Boolean? = null,
    var csDropDate: LocalDateTime? = LocalDateTime.of(2024, 5, 24, 5, 23)
)

enum class UserType {
    WAIT_AUTH, BAD_AUTH, AUTH_COMPLETED
}