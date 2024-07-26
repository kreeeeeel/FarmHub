package com.project.panel.service.import.impl

import com.google.gson.GsonBuilder
import com.project.panel.data.SteamData
import com.project.panel.repository.impl.UserRepository
import com.project.panel.service.import.MaFileImport
import com.project.panel.service.logger.LoggerService
import java.io.File

class DefaultMaFileImport: MaFileImport {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun filterFiles(files: List<File>): List<File> =
        files.filter { f -> f.isFile && getDataFromFile(f) != null && !isUserExist(getDataFromFile(f)) }

    private fun getDataFromFile(file: File): SteamData? = try {
        gson.fromJson(file.reader(), SteamData::class.java)
    } catch (e: Exception) { null }

    private fun isUserExist(steam: SteamData?): Boolean {
        if (steam == null) return false
        if (UserRepository.findById(steam.accountName) != null) {
            LoggerService.getLogger().warning("User ${steam.accountName} already exists")
            return true
        }
        return false
    }

}
