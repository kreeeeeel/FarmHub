package com.project.steamfarm.repository.impl

import com.project.steamfarm.model.UserModel
import com.project.steamfarm.repository.PATH_REPOSITORY
import com.project.steamfarm.repository.Repository
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object UserRepository: Repository<UserModel> {

    override fun findAll(): List<UserModel> {
        val files = File(PATH_REPOSITORY).listFiles()
        if (files == null || files.isEmpty()) {
            return listOf()
        }

        return files.filter { it.isFile }
            .map {
                FileReader(it.absolutePath).use { reader -> gson.fromJson(reader, UserModel::class.java) }
            }
    }

    override fun findById(id: String): UserModel? {
        val file = File("$PATH_REPOSITORY\\$id.json")
        if (!file.exists()) {
            return null
        }

        return gson.fromJson(FileReader(file).use { reader -> reader.readText() }, UserModel::class.java)
    }

    override fun delete(data: UserModel) = File("$PATH_REPOSITORY\\${data.steam.accountName}.json").let {
        if (it.exists()) { it.delete() }
    }

    override fun save(data: UserModel) {
        val file = File("$PATH_REPOSITORY\\${data.steam.accountName}.json").apply { parentFile.mkdirs() }
        FileWriter(file).use { writer ->
            writer.write(gson.toJson(data))
            writer.flush()
        }
    }

}