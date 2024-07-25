package com.project.panel.service.import.impl

import com.google.gson.GsonBuilder
import com.project.panel.data.SteamData
import com.project.panel.model.UserModel
import com.project.panel.service.import.PasswordImport
import com.project.panel.service.logger.LoggerService
import java.io.File
import java.io.FileReader

class DefaultPasswordImport: PasswordImport {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun getPasswordsFromFile(file: File, maFiles: List<File>): List<UserModel> {
        try {
            val users = maFiles.mapNotNull { getDataFromFile(it) }
                .associateBy { it.accountName }

            if (users.isEmpty()) {
                LoggerService.getLogger().warning("No users found")
                return emptyList()
            }

            FileReader(file).use { reader ->
                reader.readLines().forEach {

                    val lastIndexOf = it.lastIndexOf(":")
                    if (lastIndexOf == -1) return@forEach
                    val login = it.substring(0, lastIndexOf).lowercase()
                    val password = it.substring(lastIndexOf + 1).trim()

                    if (users.containsKey(login)) {
                        users[login]?.password = password
                        LoggerService.getLogger().info("Found password for account: $login")
                    }
                }
            }
            return users.values.filter { isUserHavePassword(it) }.map { UserModel(steam = it) }.toList()
        } catch (e: Exception) { return emptyList() }
    }

    private fun getDataFromFile(file: File): SteamData? =try {
        gson.fromJson(file.reader(), SteamData::class.java)
    } catch (e: Exception) { null }

    private fun isUserHavePassword(steamData: SteamData): Boolean {
        if (steamData.password == null) {
            LoggerService.getLogger().error("Password not found for user: ${steamData.accountName}")
            return false
        }
        return true
    }

}
