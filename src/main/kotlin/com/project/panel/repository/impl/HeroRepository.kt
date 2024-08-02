package com.project.panel.repository.impl

import com.project.panel.AppRun
import com.project.panel.exception.ResourcesException
import com.project.panel.model.HeroModel
import com.project.panel.repository.Repository
import com.project.panel.service.logger.LoggerService
import javafx.scene.image.Image
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.name
import kotlin.io.path.pathString

private const val PATH_HERO = "heroes"
private const val PATH_HERO_IMAGES = "heroes/images"

object HeroRepository: Repository<HeroModel> {

    private val heroes: List<HeroModel>

    init {
        val resource = AppRun::class.java.classLoader.getResource(PATH_HERO) ?: throw ResourcesException()
        heroes = Files.list(Paths.get(resource.toURI())).use { files ->
            files.toList().filter { Files.isRegularFile(it) }
                .map { file -> gson.fromJson(Files.readString(file), HeroModel::class.java) }
        }

        LoggerService.getLogger().info("Initializing ${heroes.size} heroes")
    }

    override fun findAll(): List<HeroModel> = heroes

    override fun findById(id: String): HeroModel? {
        LoggerService.getLogger().debug("Find hero by id: $id")
        return heroes.firstOrNull { it.name == id }.also {
            if (it == null) LoggerService.getLogger().warning("Hero $id not found!")
        }
    }

    override fun save(data: HeroModel) {}
    override fun delete(data: HeroModel) {}
}

object HeroImageRepository: Repository<Image> {

    private val images: HashMap<String, Image>

    init {
        val resource = AppRun::class.java.classLoader.getResource(PATH_HERO_IMAGES) ?: throw ResourcesException()
        images = Files.list(Paths.get(resource.toURI())).use { files ->
            files.toList().associate {
                it.name to Image(it.pathString)
            }.toMap(HashMap())
        }

        LoggerService.getLogger().info("Initializing ${images.size} images heroes")
    }

    override fun findAll(): List<Image> {
        return emptyList()
    }

    override fun findById(id: String): Image? {
        LoggerService.getLogger().debug("Find hero image by id: $id")
        return images[id].also {
            if (it == null) LoggerService.getLogger().warning("Image for hero $id not found!")
        }
    }

    override fun save(data: Image) {}
    override fun delete(data: Image) {}
}