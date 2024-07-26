package com.project.panel.exception

import com.project.panel.service.logger.LoggerService

private const val MESSAGE = "Failed to access PC processes."
class EnumProcessesException : Exception(MESSAGE) {
    init { LoggerService.getLogger().error(MESSAGE) }
}
