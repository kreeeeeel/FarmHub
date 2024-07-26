package com.project.panel.service.farm.bes.impl

import com.project.panel.exception.EnumProcessesException
import com.project.panel.service.farm.Manager.limitedPids
import com.project.panel.service.farm.bes.BesLimit
import com.project.panel.service.logger.LoggerService
import com.project.panel.service.process.Kernel32Ext
import com.project.panel.service.process.ProcessType
import com.project.panel.service.process.PsapiExt
import com.sun.jna.ptr.IntByReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

private const val PERCENT_GAME = 50
private const val PERCENT_STEAM = 95
private const val PERCENT_STEAM_OVERLAY = 100

private const val OVERLAY_STEAM_NAME = "GameOverlayUI.exe"
private const val STEAM_WEB_NAME = "steamwebhelper.exe"
private const val STEAM_NAME = "steam.exe"
private const val DOTA_NAME = "dota2.exe"

private val BES = "${System.getProperty("user.dir")}\\BES\\BES.exe"

class BesLimitImpl: BesLimit {

    init {
        if (!File(BES).exists()) throw FileNotFoundException("BES is not found!")
    }

    override suspend fun getTargetPids(): Map<ProcessType, List<Int>> {

        LoggerService.getLogger().info("Start processing PC processes")

        val startOffsetTime = Date()
        val processes = IntArray(1024)
        val pBytesReturned = IntByReference()

        if (!PsapiExt.INSTANCE.EnumProcesses(processes, processes.size * 4, pBytesReturned)) {
            throw EnumProcessesException()
        }

        val numProcesses = pBytesReturned.value / 4
        val result = withContext(Dispatchers.IO) {
            IntStream.range(0, numProcesses)
                .map { processes[it] }
                .filter { !limitedPids.contains(it) }
                .mapToObj { pid -> runBlocking { getProcessInfo(pid) } }
                .filter(Objects::nonNull)
                .map { it!! }
                .collect(Collectors.groupingBy({ it.processType }, Collectors.mapping({ it.pid }, Collectors.toList())))
        }

        val time = Date().time - startOffsetTime.time
        LoggerService.getLogger().info("End of PC Process Processing, Processing Time $time milliseconds")

        return result
    }

    private suspend fun getProcessInfo(pid: Int): ProcessInfo? = withContext(Dispatchers.IO) {
        val hProcess = Kernel32Ext.INSTANCE.OpenProcess(
            Kernel32Ext.PROCESS_QUERY_INFORMATION or Kernel32Ext.PROCESS_VM_READ,
            false,
            pid
        )
        if (hProcess == null) return@withContext null

        val processName = ByteArray(1024)
        val nameLength = PsapiExt.INSTANCE.GetProcessImageFileNameA(hProcess, processName, processName.size)
        Kernel32Ext.INSTANCE.CloseHandle(hProcess)

        if (nameLength > 0) {
            val processNameString = String(processName, 0, nameLength)
            val processType = determineProcessType(processNameString, pid)
            if (processType != null) ProcessInfo(pid, processType) else null
        } else {
            null
        }
    }

    private suspend fun determineProcessType(processName: String, pid: Int): ProcessType? = when {
        processName.contains(OVERLAY_STEAM_NAME) -> {
            LoggerService.getLogger().info("OVERLAY Steam process found: $pid PID")
            limit(pid, PERCENT_STEAM_OVERLAY)
            ProcessType.OVERLAY
        }
        processName.contains(STEAM_WEB_NAME) || processName.contains(STEAM_NAME) -> {
            LoggerService.getLogger().info("Steam process found: $pid PID")
            limit(pid, PERCENT_STEAM)
            ProcessType.STEAM
        }
        processName.contains(DOTA_NAME) -> {
            LoggerService.getLogger().info("Game process found: $pid PID")
            limit(pid, PERCENT_GAME)
            ProcessType.GAME
        }
        else -> null
    }

    private suspend fun limit(target: Int, percent: Int) = withContext(Dispatchers.IO) {
        limitedPids.add(target)

        val processBuilder = ProcessBuilder()
        val command = listOf(BES, "\"PID:$target\"", "$percent", "-m")

        LoggerService.getLogger().info("Start Battle Encoder Shirasé to limit CPU usage | PID: $target.")

        val params = StringBuilder().also { command.forEach { c -> it.append(c).append(" ") } }.trim()
        LoggerService.getLogger().debug("Battle Encoder Shirasé params: $params")

        processBuilder.command(command).start()
    }
}

private data class ProcessInfo(val pid: Int, val processType: ProcessType)