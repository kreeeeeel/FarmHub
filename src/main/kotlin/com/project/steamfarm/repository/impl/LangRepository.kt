package com.project.steamfarm.repository.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.project.steamfarm.model.LangModel
import com.project.steamfarm.repository.Repository
import java.io.File
import java.io.FileReader

private val PATH_TO_LANG = System.getProperty("user.dir") + "/lang"
private val FILE_LANG = File(PATH_TO_LANG)

object LangRepository: Repository<LangModel> {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

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

    override fun findById(id: String): LangModel? {
        val lang = File("$PATH_TO_LANG/$id.json")
        if (!lang.exists()) {
            return null
        }

        return gson.fromJson(FileReader(lang), LangModel::class.java)
    }

    override fun delete(data: LangModel) {}
    override fun save(data: LangModel) {}

}