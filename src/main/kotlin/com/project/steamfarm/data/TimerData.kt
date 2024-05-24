package com.project.steamfarm.data

import java.util.Timer

data class TimerData(
    val timer: Timer,
    val value: String?,
    val type: TimerType
)

enum class TimerType {
    WAIT_AUTH
}