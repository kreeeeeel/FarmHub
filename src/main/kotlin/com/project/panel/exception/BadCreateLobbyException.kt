package com.project.panel.exception

import com.project.panel.service.logger.LoggerService

private const val MESSAGE = "There must be ten users to create a lobby."
class BadCreateLobbyException: Exception(MESSAGE) {

    init { LoggerService.getLogger().error(MESSAGE) }
}
