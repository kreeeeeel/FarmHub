package com.project.steamfarm.service.steam

interface ClientSteam {
    fun authentication(username: String, password: String): Boolean
}