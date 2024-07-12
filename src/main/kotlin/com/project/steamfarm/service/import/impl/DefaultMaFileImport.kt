package com.project.steamfarm.service.import.impl

import com.google.gson.GsonBuilder
import com.project.steamfarm.data.SteamData
import com.project.steamfarm.service.import.MaFileImport
import java.io.File

class DefaultMaFileImport: MaFileImport {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun filterFiles(files: List<File>): List<File> =
        files.filter { f -> f.isFile && getDataFromFile(f) != null }

    private fun getDataFromFile(file: File): SteamData? = try {
            gson.fromJson(file.reader(), SteamData::class.java)
    } catch (e: Exception) { null }

}
