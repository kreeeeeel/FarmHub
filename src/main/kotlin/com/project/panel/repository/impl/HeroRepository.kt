package com.project.panel.repository.impl

import com.project.panel.model.HeroModel
import com.project.panel.repository.Repository
import com.project.panel.service.logger.LoggerService
import javafx.scene.image.Image
import java.io.File
import java.io.FileReader

private val PATH_HERO = "${System.getProperty("user.dir")}\\config\\Dota 2\\heroes"

object HeroRepository: Repository<HeroModel> {

    override fun findAll(): List<HeroModel> {
        val result = File(PATH_HERO).listFiles()?.filter { it.isFile }?.map {
            FileReader(it.absolutePath).use { reader -> gson.fromJson(reader, HeroModel::class.java) }
        } ?: listOf()

        LoggerService.getLogger().info("Search all hero from Dota2, found ${result.size} heroes.")
        return result
    }

    override fun findById(id: String): HeroModel? {
        LoggerService.getLogger().debug("Find hero by id: $id")
        val file = File("$PATH_HERO\\$id.json")
        if (!file.exists()) {
            LoggerService.getLogger().warning("Hero $id not found!")
            return null
        }

        return gson.fromJson(FileReader(file).use { reader -> reader.readText() }, HeroModel::class.java)
    }

    override fun save(data: HeroModel) {}
    override fun delete(data: HeroModel) {}
}

object HeroImageRepository: Repository<Image> {

    override fun findAll(): List<Image> {
        return emptyList()
    }

    override fun findById(id: String): Image? {
        LoggerService.getLogger().debug("Find hero image by id: $id")
        val file = File(System.getProperty("user.dir") + "\\" +id)
        return if (file.exists()) Image(file.toURI().toString())
        else null
    }

    override fun save(data: Image) {}
    override fun delete(data: Image) {}
}