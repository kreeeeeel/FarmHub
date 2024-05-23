package com.project.steamfarm.repository.impl

import com.google.gson.GsonBuilder
import com.project.steamfarm.data.SteamData
import com.project.steamfarm.repository.Repository
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private val PATH_TO_MA_FILE = System.getProperty("user.dir") + "/mafiles"

class MaFileRepository: Repository<SteamData> {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun findAll(): List<SteamData> {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): SteamData? {
        val path = String.format("%s/%s.maFile", PATH_TO_MA_FILE, id)
        val file = File(path)
        if (!file.exists()) {
            return null
        }

        return gson.fromJson(FileReader(file).use { reader -> reader.readText() }, SteamData::class.java)
    }

    override fun save(data: SteamData) {
        val path = String.format("%s/%s.maFile", PATH_TO_MA_FILE, data.accountName)

        val directory = File(PATH_TO_MA_FILE)
        if (!directory.exists() && directory.mkdirs()) {
            println("Создание хранилища с данными .maFile")
        }

        val file = File(path)
        FileWriter(file).use { writer ->
            writer.write(gson.toJson(data))
            writer.flush()
        }
    }
}