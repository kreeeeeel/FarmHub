package com.project.panel.exception

import com.project.panel.service.logger.LoggerService

private const val MESSAGE = "The lobby must consist of five people."
class BadSizeLobbyException: Exception(MESSAGE) {

    init { LoggerService.getLogger().error(MESSAGE) }
}
