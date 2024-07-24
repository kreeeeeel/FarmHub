package com.project.steamfarm.service.farm.bes

import com.project.steamfarm.service.process.ProcessType

interface BesLimit {
    suspend fun getTargetPids(): Map<ProcessType, List<Int>>
}