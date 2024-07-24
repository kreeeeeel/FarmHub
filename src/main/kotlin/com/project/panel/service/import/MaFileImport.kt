package com.project.panel.service.import

import java.io.File

interface MaFileImport {
    fun filterFiles(files: List<File>): List<File>
}
