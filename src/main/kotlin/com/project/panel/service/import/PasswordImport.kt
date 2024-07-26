package com.project.panel.service.import

import com.project.panel.model.UserModel
import java.io.File

interface PasswordImport {
    fun getPasswordsFromFile(file: File, maFiles: List<File>): List<UserModel>
}
