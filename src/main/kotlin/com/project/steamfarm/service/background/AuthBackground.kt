package com.project.steamfarm.service.background

interface AuthBackground {
    fun authenticate(username: String, password: String)
}