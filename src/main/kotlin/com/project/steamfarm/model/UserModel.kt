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
    var lastDropCsDate: LocalDateTime? = null,
) {
    private var _priorityHero: MutableList<String> = MutableList(6) { "random" }

    var priorityHero: MutableList<String>
        get() = _priorityHero
        set(value) {
            _priorityHero = if (value.size > 6) value.take(6).toMutableList() else {
                value.toMutableList().apply {
                    while (this.size < 6) {
                        add("default")
                    }
                }
            }
        }
}