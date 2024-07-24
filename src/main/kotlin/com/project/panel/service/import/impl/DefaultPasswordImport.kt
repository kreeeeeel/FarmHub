package com.project.panel.service.import.impl

import com.google.gson.GsonBuilder
import com.project.panel.data.SteamData
import com.project.panel.model.UserModel
import com.project.panel.service.import.PasswordImport
import java.io.File
import java.io.FileReader

class DefaultPasswordImport: PasswordImport {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun getPasswordsFromFile(file: File, maFiles: List<File>): List<UserModel> {
        try {
            val users = maFiles.mapNotNull { getDataFromFile(it) }
                .associateBy { it.accountName }

            FileReader(file).use { reader ->
                reader.readLines().forEach {
                    val split = it.split(":")

                    val login = split[0]
                    val password = split[1]

                    if (split.size == 2 && users.containsKey(login)) {
                        users[login]?.password = password
                    }
                }
            }
            return users.values.map { UserModel(steam = it) }.toList()
        } catch (e: Exception) { return emptyList() }
    }

    private fun getDataFromFile(file: File): SteamData? =try {
        gson.fromJson(file.reader(), SteamData::class.java)
    } catch (e: Exception) { null }

}
