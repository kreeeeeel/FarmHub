package com.project.steamfarm.model

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileReader
import java.io.FileWriter

private val FILE_CONFIG = File(System.getProperty("user.dir") + "\\config\\config.json")

data class ConfigModel(
    var langApp: String = DEFAULT_LANGUAGE,
    var steamExecutor: String? = null
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
