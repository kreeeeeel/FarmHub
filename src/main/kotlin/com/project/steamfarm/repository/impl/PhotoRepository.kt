package com.project.steamfarm.repository.impl

import com.project.steamfarm.model.UserModel
import com.project.steamfarm.repository.Repository
import javafx.scene.image.Image
import java.io.*
import java.net.URL

private val PHOTO_PATH = System.getProperty("user.dir") + "/users"
private const val PHOTO_FILE_NAME = "photo.png"

class PhotoRepository: Repository<Image> {

    private val userRepository: Repository<UserModel> = UserRepository()

    override fun findAll(): List<Image> {
        return listOf()
    }

    override fun findById(id: String): Image? {

        val path = String.format("%s/%s/%s", PHOTO_PATH, id, PHOTO_FILE_NAME)
        val file = File(path)
        if (!file.exists()) {
            return getPhotoFromLink(id)
        }

        return Image(file.toURI().toString())

    }

    override fun save(data: Image) {}

    private fun getPhotoFromLink(username: String): Image? {
        val userModel = userRepository.findById(username) ?: return null

        val link = userModel.photo ?: return null
        val bytes = getBytesFromUrl(link) ?: return null
        val inputStream = ByteArrayInputStream(bytes)

        val result = Image(inputStream)

        val path = String.format("%s/%s/%s", PHOTO_PATH, username, PHOTO_FILE_NAME)
        val file = File(path).also { it.createNewFile() }

        FileOutputStream(file).use { outputStream -> outputStream.write(bytes) }
        return result
    }

    private fun getBytesFromUrl(url: String): ByteArray? {
        try {
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
            return null
        }
    }
}