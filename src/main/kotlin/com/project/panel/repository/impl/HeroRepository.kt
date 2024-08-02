package com.project.panel.repository.impl

import com.project.panel.model.HeroModel
import com.project.panel.repository.Repository
import com.project.panel.service.logger.LoggerService
import javafx.scene.image.Image
import java.io.File
import java.io.FileReader

private val PATH_HERO = "${System.getProperty("user.dir")}\\config\\Dota 2\\heroes"

object HeroRepository: Repository<HeroModel> {

    private val heroes: List<HeroModel> = File(PATH_HERO).listFiles()?.filter { it.isFile }?.map {
        FileReader(it.absolutePath).use { reader -> gson.fromJson(reader, HeroModel::class.java) }
    } ?: listOf()

    init { LoggerService.getLogger().info("Initializing ${heroes.size} Dota 2 heroes") }

    override fun findAll(): List<HeroModel> = heroes

    override fun findById(id: String): HeroModel? {
        LoggerService.getLogger().debug("Find hero by id: $id")

        val hero = heroes.firstOrNull { it.name == id }
        if (hero == null) { LoggerService.getLogger().warning("Hero $id not found!") }
        return hero
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