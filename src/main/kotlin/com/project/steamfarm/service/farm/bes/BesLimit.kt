package com.project.steamfarm.service.farm.bes

interface BesLimit {
    suspend fun limit(target: Int)
}