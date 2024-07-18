package com.project.steamfarm.service.logger

import com.project.steamfarm.service.logger.impl.LoggerServiceImpl

interface LoggerService {
    fun info(text: String)
    fun debug(text: String)
    fun warning(text: String)
    fun error(text: String)

    companion object {
        private val logger = LoggerServiceImpl()

        fun getLogger() = logger
    }
}