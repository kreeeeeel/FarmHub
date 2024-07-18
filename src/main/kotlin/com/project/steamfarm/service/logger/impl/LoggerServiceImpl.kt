package com.project.steamfarm.service.logger.impl

import com.project.steamfarm.service.logger.LoggerService
import java.io.File
import java.io.FileWriter
import java.time.OffsetDateTime

private val PATH = "${System.getProperty("user.dir")}\\logs"

class LoggerServiceImpl: LoggerService {

    override fun info(text: String) = log(text, LogLevel.INFO)
    override fun debug(text: String) = log(text, LogLevel.DEBUG)
    override fun warning(text: String) = log(text, LogLevel.WARNING)
    override fun error(text: String) = log(text, LogLevel.ERROR)

    private fun log(text: String, logLevel: LogLevel) {
        val currentDate = OffsetDateTime.now()
        val logFile = getLogFile()

        val output = String.format("[%02d:%02d:%02d] %s: %s",
            currentDate.hour, currentDate.minute, currentDate.second, logLevel, text)

        println(output)
        FileWriter(logFile, true).use { it.write(output + System.lineSeparator()) }
    }

    private fun getLogFile(): File {
        val currentDate = OffsetDateTime.now()
        val path = String.format("%02d-%02d-%d.txt",
            currentDate.year,
            currentDate.month.value,
            currentDate.dayOfMonth
        )

        return File("$PATH\\$path").apply { parentFile.mkdirs() }
    }

}

private enum class LogLevel { INFO, WARNING, ERROR, DEBUG }
