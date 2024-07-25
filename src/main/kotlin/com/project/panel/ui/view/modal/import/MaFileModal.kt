package com.project.panel.ui.view.modal.import

import com.project.panel.langApplication
import com.project.panel.model.UserModel
import com.project.panel.service.import.MaFileImport
import com.project.panel.service.import.impl.DefaultMaFileImport
import com.project.panel.ui.controller.BaseController.Companion.root
import com.project.panel.ui.view.notify.NotifyView
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

class MaFileModal(
    private val action: (UserModel) -> Unit
): ImportModal(MAFILE_MODAL_TYPE) {

    private val maFileImport: MaFileImport = DefaultMaFileImport()
    private val notifyView = NotifyView()

    override fun fileChooser(): Unit? = FileChooser().also {
        it.title = "${langApplication.text.accounts.maFile.file} .maFile"
        it.extensionFilters.add(FileChooser.ExtensionFilter("Ma File", "*.maFile"))

        configModel.lastDirectoryChooser?.let { dir ->
            val file = File(dir)
            if (file.exists()) it.initialDirectory = File(dir)
        }
    }.showOpenMultipleDialog(root.scene.window as Stage)?.let { handler(it) }

    override fun handler(files: List<File>) = maFileImport.filterFiles(files).let {

        configModel.lastDirectoryChooser = files[0].parentFile.absolutePath
        configModel.save()

        if (it.isNotEmpty()) {
            root.children.removeIf { node -> node.id == window.id }
            PasswordFileModal(it, action).show()
            notifyView.success(langApplication.text.success.maFile)
        } else notifyView.failure(langApplication.text.failure.maFile)
    }

}