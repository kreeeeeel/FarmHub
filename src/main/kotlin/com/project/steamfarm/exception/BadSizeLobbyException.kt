package com.project.steamfarm.exception

import com.project.steamfarm.service.logger.LoggerService

private const val MESSAGE = "The lobby must consist of five people."
class BadSizeLobbyException: Exception(MESSAGE) {

    init { LoggerService.getLogger().error(MESSAGE) }
}
