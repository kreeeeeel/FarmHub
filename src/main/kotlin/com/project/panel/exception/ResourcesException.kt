package com.project.panel.exception

import com.project.panel.service.logger.LoggerService

private const val MESSAGE = "Failed to load resources."
class ResourcesException : Exception(MESSAGE) {
    init { LoggerService.getLogger().error(MESSAGE) }
}
