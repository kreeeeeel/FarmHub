package com.project.panel.model

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private val FILE_CONFIG = File(System.getProperty("user.dir") + "\\config\\config.json")

const val WIDTH_APP = 384
const val HEIGHT_APP = 216

data class ConfigModel(
    var langApp: String = DEFAULT_LANGUAGE,
    var lastDirectoryChooser: String? = null,
    var steamExecutor: String? = null,
    var discordActivity: Boolean = true
) {

    @Transient
    private val gson = GsonBuilder().setPrettyPrinting()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    fun fromFile(): ConfigModel = try {
        gson.fromJson(FileReader(FILE_CONFIG), ConfigModel::class.java)
    } catch (e: Exception) { this }

    fun save() = FileWriter(FILE_CONFIG).use {
        writer -> writer.write(gson.toJson(this))
    }

}
