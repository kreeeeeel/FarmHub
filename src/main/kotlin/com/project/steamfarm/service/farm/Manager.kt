package com.project.steamfarm.service.farm

import com.project.steamfarm.data.LobbyUserData
import com.project.steamfarm.exception.BadCreateLobbyException
import com.project.steamfarm.exception.BadImplementationGame
import com.project.steamfarm.exception.BadSizeLobbyException
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.service.farm.bes.BesLimit
import com.project.steamfarm.service.farm.bes.impl.BesLimitImpl
import com.project.steamfarm.service.farm.steam.GameDesktop
import com.project.steamfarm.service.farm.steam.SteamDesktop
import com.project.steamfarm.service.farm.steam.impl.DotaGameDesktop
import com.project.steamfarm.service.farm.steam.impl.SteamDesktopImpl
import com.project.steamfarm.service.logger.LoggerService
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.ptr.IntByReference
import kotlinx.coroutines.*

val STEAM_PATH = "$PATH_TO_IMG\\steam"

const val SWP_NOSIZE = 0x0001
const val SWP_NOZORDER = 0x0004

object Manager: Desktop() {

    private val steamDesktop: SteamDesktop = SteamDesktopImpl()
    private val dotaDesktop: GameDesktop = DotaGameDesktop()
    private val besLimit: BesLimit = BesLimitImpl()

    // Teams
    private var teamA: MutableList<LobbyUserData> = mutableListOf()
    private var teamB: MutableList<LobbyUserData> = mutableListOf()

    // Variables for position on window
    private var offsetVertical = 0
    private var offsetHorizontal = 0

    suspend fun testByKrel(userModel: List<UserModel>) {
        teamA = userModel.map { LobbyUserData.mapper(it) }.toMutableList()
        val users = teamA + teamB
        users.forEach { launchGame(it) }

        //delay(5000)
        //shuffleLobby()
    }

    suspend fun initGame(gameId: Int) {
        when (gameId) {
            570 -> steamDesktop.initGame(dotaDesktop)
            else -> throw BadImplementationGame()
        }
    }

    fun initLobby(teamA: Set<UserModel>, teamB: Set<UserModel>) {
        if (teamA.size != 5 || teamB.size != 5) throw BadCreateLobbyException()

        this.teamA = teamA.map { LobbyUserData.mapper(it) }.toMutableList()
        this.teamB = teamB.map { LobbyUserData.mapper(it) }.toMutableList()

        LoggerService.getLogger().info("Creating a lobby specifying users.")
        val compositionA = Manager.teamA.joinToString(", ") { it.userModel.steam.accountName }
        LoggerService.getLogger().info("Team A composition: $compositionA")

        val compositionB = Manager.teamB.joinToString(", ") { it.userModel.steam.accountName }
        LoggerService.getLogger().info("Team B composition: $compositionB")
    }

    fun initRandomLobby(userModels: Set<UserModel>) {
        if (userModels.size != 10) throw BadCreateLobbyException()

        val shuffledUsers = userModels.shuffled()
        teamA = shuffledUsers.subList(0, 5).map { LobbyUserData.mapper(it) }.toMutableList()
        teamB = shuffledUsers.subList(5, 10).map { LobbyUserData.mapper(it) }.toMutableList()

        LoggerService.getLogger().info("Creating a random lobby.")
        val compositionA = teamA.joinToString(", ") { it.userModel.steam.accountName }
        LoggerService.getLogger().info("Team A composition: $compositionA")

        val compositionB = teamB.joinToString(", ") { it.userModel.steam.accountName }
        LoggerService.getLogger().info("Team B composition: $compositionB")
    }

    /*fun shuffleLobby() {
        val shuffled = (teamA + teamB).shuffled()

        offsetVertical = 0
        offsetHorizontal = 0

        shuffled.forEach { setOffsetHwnd(it.userHWND) }
    }*/

    fun start() = runBlocking {
        if (teamA.size != 5 || teamB.size != 5) throw BadSizeLobbyException()
        (teamA + teamB).forEachIndexed { index, lobbyUser ->
            LoggerService.getLogger().info("Start farm user #${index + 1}")
            launchGame(lobbyUser)
        }
    }

    suspend fun inviteToLobby() {
        teamA.subList(1, 5).forEach {
            steamDesktop.gameDesktop.sendInvite(teamA[0].userHWND, "${it.userModel.steam.session!!.steamID}")
            steamDesktop.gameDesktop.acceptInvite(it.userHWND)
        }

        teamB.subList(1, 5).forEach {
            steamDesktop.gameDesktop.sendInvite(teamB[0].userHWND, "${it.userModel.steam.session!!.steamID}")
            steamDesktop.gameDesktop.acceptInvite(it.userHWND)
        }
    }

    private suspend fun launchGame(lobbyUserData: LobbyUserData) {

        val searchTeam = searchTeam(lobbyUserData)

        steamDesktop.start(lobbyUserData.userModel.steam.accountName)
        steamDesktop.signIn(lobbyUserData.userModel.steam.accountName, lobbyUserData.userModel.steam.password)

        val isEntered = steamDesktop.guard(lobbyUserData.userModel.steam.sharedSecret)
        if (!isEntered) {
            LoggerService.getLogger().warning("User ${lobbyUserData.userModel.steam.accountName} is not entered!")
        } else {
            LoggerService.getLogger().info("User ${lobbyUserData.userModel.steam.accountName} is entered!")
        }

        val scope = CoroutineScope(Dispatchers.Default)
        val jobs = listOf(
            scope.launch { steamDesktop.gameDesktop.closeSupport() },
            scope.launch { steamDesktop.gameDesktop.closeCloudConflict() }
        )

        lobbyUserData.userHWND = steamDesktop.gameDesktop.getGameHwnd()
        jobs.forEach { it.cancel() }

        steamDesktop.gameDesktop.setReadyGame(lobbyUserData.userHWND)
        steamDesktop.gameDesktop.setName(lobbyUserData.userHWND, lobbyUserData.userModel.steam.accountName)

        setOffsetHwnd(lobbyUserData.userHWND)
        limitBesByHwnd(lobbyUserData.userHWND)

        searchTeam.first.add(searchTeam.second, lobbyUserData)
    }

    private fun setOffsetHwnd(hwnd: HWND) {
        val offsetProperties = getOffsetProperties(hwnd)
        val offsetX = offsetHorizontal++ * offsetProperties.width
        val offsetY = offsetVertical * offsetProperties.height

        if (offsetHorizontal >= 5) {
            offsetHorizontal = 0
            offsetVertical++
        }

        LoggerService.getLogger().info("Changing the Dota2 window position | X=$offsetX Y=$offsetY")
        User32Ext.INSTANCE.SetWindowPos(hwnd, null, offsetX, offsetY, 0, 0, SWP_NOSIZE or SWP_NOZORDER)
    }

    private suspend fun limitBesByHwnd(hwnd: HWND) {
        val currentPid = IntByReference()
        User32Ext.INSTANCE.GetWindowThreadProcessId(hwnd, currentPid)
        besLimit.limit(currentPid.value)
    }

    private fun searchTeam(lobbyUserData: LobbyUserData): Pair<MutableList<LobbyUserData>, Int> {
        val indexOf = teamA.indexOf(lobbyUserData)
        return if (indexOf != -1) Pair(teamA, indexOf)
        else Pair(teamB, teamB.indexOf(lobbyUserData))
    }
}
