package com.project.panel.repository.impl

import com.project.panel.repository.PATH_REPOSITORY
import com.project.panel.repository.Repository
import com.project.panel.service.logger.LoggerService
import javafx.scene.image.Image
import java.io.*
import java.net.URL

private val CACHE_PATH = "$PATH_REPOSITORY\\cache"

object CacheRepository: Repository<Image> {

    override fun findAll(): List<Image> {
        return listOf()
    }

    // id = username
    override fun findById(id: String): Image? {
        LoggerService.getLogger().info("Search photo user for $id")
        val file = File("${CACHE_PATH}\\$id.png")
        if (!file.exists()) {
            return getPhotoFromLink(id)
        }
        return Image(file.toURI().toString())
    }

    override fun delete(data: Image) {}
    override fun save(data: Image) {}

    private fun getPhotoFromLink(username: String): Image? {
        val userModel = UserRepository.findById(username) ?: return null

        val link = userModel.photo ?: return null
        val bytes = getBytesFromUrl(link) ?: return null
        val inputStream = ByteArrayInputStream(bytes)

        val result = Image(inputStream)
        val file = File("${CACHE_PATH}\\$username.png").apply {
            parentFile.mkdirs()
            createNewFile()
        }

        LoggerService.getLogger().info("Write $username photo to cache")
        FileOutputStream(file).use { outputStream -> outputStream.write(bytes) }
        return result
    }

    @Suppress("DEPRECATION")
    private fun getBytesFromUrl(url: String): ByteArray? {
        try {
            LoggerService.getLogger().info("Download photo user from $url")
            val outputStream = ByteArrayOutputStream()
            URL(url).openStream().use { inputStream ->
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
            return outputStream.toByteArray()
        } catch (e: IOException) {
            LoggerService.getLogger().error("Failed to download image from $url")
            return null
        }
    }
}