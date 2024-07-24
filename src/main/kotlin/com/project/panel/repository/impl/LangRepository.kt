package com.project.panel.repository.impl

import com.project.panel.model.LangModel
import com.project.panel.repository.Repository
import java.io.File
import java.io.FileReader

val PATH_LANGUAGES = "${System.getProperty("user.dir")}\\config\\Languages"
private val FILE_LANG = File(PATH_LANGUAGES)

object LangRepository: Repository<LangModel> {

    override fun findAll(): List<LangModel> =
        FILE_LANG.listFiles()?.filter { it.name.endsWith(".json") }
            ?.mapNotNull {
                gson.fromJson(FileReader(it).use { reader -> reader.readText() }, LangModel::class.java)
                    .also { l ->
                        if (l == null) return@mapNotNull null
                        l.code = it.name.substring(0, it.name.lastIndexOf('.'))
                    }
            } ?: emptyList()

    // id = country code
    override fun findById(id: String): LangModel? {
        val lang = File("$PATH_LANGUAGES\\$id.json")
        if (!lang.exists()) {
            return null
        }

        return gson.fromJson(FileReader(lang), LangModel::class.java)
    }

    override fun delete(data: LangModel) {}
    override fun save(data: LangModel) {}

}