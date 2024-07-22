package com.project.steamfarm.exception

import com.project.steamfarm.service.logger.LoggerService

private const val MESSAGE = "There is no implementation for this game."
class BadImplementationGame: Exception(MESSAGE) {

    init { LoggerService.getLogger().error(MESSAGE) }
}
