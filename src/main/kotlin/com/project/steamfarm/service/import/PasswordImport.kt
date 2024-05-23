package com.project.steamfarm.service.import

import com.project.steamfarm.model.UserModel
import java.io.File

interface PasswordImport {

    fun getPasswordsFromFile(file: File, maFiles: List<File>): List<UserModel>

}