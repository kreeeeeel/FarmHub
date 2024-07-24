package com.project.steamfarm.exception

import com.project.steamfarm.service.logger.LoggerService

private const val MESSAGE = "Failed to access PC processes."
class EnumProcessesException : Exception(MESSAGE) {
    init { LoggerService.getLogger().error(MESSAGE) }
}
