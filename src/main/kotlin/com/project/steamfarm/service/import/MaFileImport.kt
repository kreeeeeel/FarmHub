package com.project.steamfarm.service.import

import java.io.File

interface MaFileImport {
    fun filterFiles(files: List<File>): List<File>
}
