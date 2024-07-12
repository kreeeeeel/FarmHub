package com.project.steamfarm.repository.impl

import com.project.steamfarm.model.HeroModel
import com.project.steamfarm.repository.Repository
import javafx.scene.image.Image
import java.io.File
import java.io.FileReader

private val PATH_HERO = "${System.getProperty("user.dir")}\\config\\Dota 2\\heroes"

object HeroRepository: Repository<HeroModel> {

    override fun findAll(): List<HeroModel> {
        val file = File(PATH_HERO)
        val files = file.listFiles()
        if (files == null || files.isEmpty()) {
            return listOf()
        }

        return files.filter { it.isFile }
            .map {
                FileReader(it.absolutePath).use { reader -> gson.fromJson(reader, HeroModel::class.java) }
            }
    }

    override fun findById(id: String): HeroModel? {
        val file = File("$PATH_HERO\\$id.json")
        if (!file.exists()) {
            return null
        }

        return UserRepository.gson.fromJson(FileReader(file).use { reader -> reader.readText() }, HeroModel::class.java)
    }

    override fun save(data: HeroModel) {}
    override fun delete(data: HeroModel) {}
}

object HeroImageRepository: Repository<Image> {

    override fun findAll(): List<Image> {
        return emptyList()
    }

    override fun findById(id: String): Image? {
        val file = File(System.getProperty("user.dir") + "\\" +id)
        return if (file.exists())
            Image(file.toURI().toString())
        else null
    }

    override fun save(data: Image) {}
    override fun delete(data: Image) {}
}