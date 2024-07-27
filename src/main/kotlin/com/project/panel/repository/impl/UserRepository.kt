package com.project.panel.repository.impl

import com.project.panel.model.UserModel
import com.project.panel.repository.PATH_REPOSITORY
import com.project.panel.repository.Repository
import com.project.panel.service.logger.LoggerService
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object UserRepository: Repository<UserModel> {

    override fun findAll(): List<UserModel> {
        val result = File(PATH_REPOSITORY).listFiles()?.filter { it.isFile }
            ?.map {
                FileReader(it.absolutePath).use { reader -> gson.fromJson(reader, UserModel::class.java) }
            } ?: emptyList()

        LoggerService.getLogger().info("Search all users, ${result.size} users found")
        return result
    }

    override fun findById(id: String): UserModel? {
        LoggerService.getLogger().info("Search user: $id")
        val file = File("$PATH_REPOSITORY\\$id.json")
        if (!file.exists()) {
            LoggerService.getLogger().warning("User $id does not exist")
            return null
        }

        return gson.fromJson(FileReader(file).use { reader -> reader.readText() }, UserModel::class.java)
    }

    override fun delete(data: UserModel) = File("$PATH_REPOSITORY\\${data.steam.accountName}.json").let {
        LoggerService.getLogger().info("Delete user: ${data.steam.accountName}")
        if (it.exists()) { it.delete() }
    }

    override fun save(data: UserModel) {
        LoggerService.getLogger().info("Save user: ${data.steam.accountName}")
        val file = File("$PATH_REPOSITORY\\${data.steam.accountName}.json").apply { parentFile.mkdirs() }
        FileWriter(file).use { writer ->
            writer.write(gson.toJson(data))
            writer.flush()
        }
    }

}