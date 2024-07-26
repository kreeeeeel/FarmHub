package com.project.panel.service.farm.bes

import com.project.panel.service.process.ProcessType

interface BesLimit {
    suspend fun getTargetPids(): Map<ProcessType, List<Int>>
}