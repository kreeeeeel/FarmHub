package com.project.panel.service.steam

interface GuardSteam {
    fun getCode(sharedSecret: String): String
}