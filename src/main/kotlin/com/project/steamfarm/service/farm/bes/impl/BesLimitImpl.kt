package com.project.steamfarm.service.farm.bes.impl

import com.project.steamfarm.service.farm.bes.BesLimit
import com.project.steamfarm.service.logger.LoggerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

private const val PERCENT = 65
private val BES = "${System.getProperty("user.dir")}\\BES\\BES.exe"

class BesLimitImpl: BesLimit {

    init {
        if (!File(BES).exists()) throw FileNotFoundException("BES is not found!")
    }

    override suspend fun limit(target: Int) {

        val processBuilder = ProcessBuilder()
        val command = listOf(BES, "\"PID:$target\"", "$PERCENT", "-m")

        withContext(Dispatchers.IO) {
            LoggerService.getLogger().info("Start Battle Encoder Shirasé to limit CPU usage.")

            val params = StringBuilder().also { command.forEach { c -> it.append(c).append(" ") } }.trim()
            LoggerService.getLogger().debug("Battle Encoder Shirasé params: $params")

            processBuilder.command(command).start()
        }

    }

}