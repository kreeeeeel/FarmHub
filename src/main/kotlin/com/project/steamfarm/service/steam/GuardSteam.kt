package com.project.steamfarm.service.steam

interface GuardSteam {
    fun getCode(sharedSecret: String): String
}