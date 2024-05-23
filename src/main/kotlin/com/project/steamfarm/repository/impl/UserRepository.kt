package com.project.steamfarm.repository.impl

import com.google.gson.GsonBuilder
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.repository.Repository
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private const val USER_PATH = "/users"
private const val USER_INFO = "/user.json"

class UserRepository: Repository<UserModel> {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun findAll(): List<UserModel> {
        val path = String.format("%s/%s", System.getProperty("user.dir"), USER_PATH)

        val files = File(path).listFiles()
        if (files == null || files.isEmpty()) {
            return listOf()
        }

        return files.map {
            FileReader( String.format("%s/%s", it.absolutePath, USER_INFO) ).use { reader ->
                gson.fromJson(reader, UserModel::class.java)
            }
        }
    }

    override fun findById(id: String): UserModel? {
        val path = String.format("%s/%s/%s/%s", System.getProperty("user.dir"), USER_PATH, id, USER_INFO)
        val file = File(path)
        if (!file.exists()) {
            return null
        }

        return gson.fromJson(FileReader(file).use { reader -> reader.readText() }, UserModel::class.java)
    }

    override fun save(data: UserModel) {
        val path = String.format("%s/%s/%s", System.getProperty("user.dir"), USER_PATH, data.username)

        val directory = File(path)
        if (!directory.exists() && !directory.mkdirs()) {
            return
        }

        val file = File(String.format("%s%s", path, USER_INFO))
        FileWriter(file).use { writer ->
            writer.write(gson.toJson(data))
            writer.flush()
        }
    }
}