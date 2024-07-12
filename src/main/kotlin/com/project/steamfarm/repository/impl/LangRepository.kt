package com.project.steamfarm.repository.impl

import com.project.steamfarm.model.LangModel
import com.project.steamfarm.repository.PATH_REPOSITORY
import com.project.steamfarm.repository.Repository
import java.io.File
import java.io.FileReader

private val PATH_LANGUAGES = "$PATH_REPOSITORY\\languages"
private val FILE_LANG = File(PATH_LANGUAGES)

object LangRepository: Repository<LangModel> {

    override fun findAll(): List<LangModel> {
        val lang = FILE_LANG.listFiles() ?: return emptyList()

        return lang.filter { it.name.endsWith(".json") }
            .mapNotNull {
                gson.fromJson(FileReader(it).use { reader -> reader.readText() }, LangModel::class.java)
                    .also { l ->
                        if (l == null) return@mapNotNull null
                        l.code = it.name.substring(0, it.name.lastIndexOf('.'))
                    }
            }
    }

    // id = country code
    override fun findById(id: String): LangModel? {
        val lang = File("$PATH_LANGUAGES/$id.json")
        if (!lang.exists()) {
            return null
        }

        return gson.fromJson(FileReader(lang), LangModel::class.java)
    }

    override fun delete(data: LangModel) {}
    override fun save(data: LangModel) {}

}