package com.project.panel.service.logger.impl

import com.project.panel.NAME_APPLICATION
import com.project.panel.VERSION_APPLICATION
import com.project.panel.service.logger.LoggerService
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.OffsetDateTime

private val PATH = "${System.getProperty("user.dir")}\\logs"

class LoggerServiceImpl: LoggerService {

    init {
        printBanner()
        FileWriter(getLogFile(), true).use { it.write(System.lineSeparator()) }
        log("Start logging", LogLevel.INFO)
    }

    private var level: LogLevel = LogLevel.DEBUG

    override fun info(text: String) = log(text, LogLevel.INFO)
    override fun debug(text: String) = log(text, LogLevel.DEBUG)
    override fun warning(text: String) = log(text, LogLevel.WARNING)
    override fun error(text: String) = log(text, LogLevel.ERROR)

    private fun log(text: String, logLevel: LogLevel) {
        if (level != LogLevel.NONE || (logLevel == LogLevel.DEBUG && level == LogLevel.DEBUG)) {

            val currentDate = OffsetDateTime.now()
            val logFile = getLogFile()

            val threadName = Thread.currentThread().name
            val callerClassName = getCallerClassName()

            val output = String.format("[%04d-%02d-%02d %02d:%02d:%02d] %s - %s",
                currentDate.year, currentDate.monthValue, currentDate.dayOfMonth,
                currentDate.hour, currentDate.minute, currentDate.second,
                logLevel, text
            )

            val console = String.format(
                "%04d-%02d-%02d %02d:%02d:%02d %-7s [%s] %s - %s",
                currentDate.year, currentDate.monthValue, currentDate.dayOfMonth,
                currentDate.hour, currentDate.minute, currentDate.second,
                getColoredOutput(logLevel.toString(), logLevel), threadName, callerClassName, text
            )

            println(console)
            FileWriter(logFile, true).use { it.write(output + System.lineSeparator()) }
        }
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

    private fun getColoredOutput(output: String, logLevel: LogLevel): String {
        val color = when (logLevel) {
            LogLevel.INFO -> "\u001B[32m"
            LogLevel.WARNING -> "\u001B[33m"
            LogLevel.ERROR -> "\u001B[31m"
            LogLevel.DEBUG -> "\u001B[34m"
            else -> "\u001B[0m"
        }
        return "$color$output\u001B[0m"
    }

    private fun getCallerClassName(): String {
        val stackTrace = Thread.currentThread().stackTrace
        for (element in stackTrace) {
            if (element.className != this::class.java.name && element.className.indexOf("java.lang.Thread") != 0) {
                return element.className
            }
        }
        return "UnknownClass"
    }

    private fun printBanner() {
        val classLoader = this::class.java.classLoader
        val resource = classLoader.getResource("banner.txt")
        if (resource != null) {
            val path = Paths.get(resource.toURI())
            val banner = Files.readAllLines(path).joinToString(System.lineSeparator())
            println(String.format(banner, NAME_APPLICATION, VERSION_APPLICATION))
        }
    }

}

private enum class LogLevel { NONE, INFO, WARNING, ERROR, DEBUG }
