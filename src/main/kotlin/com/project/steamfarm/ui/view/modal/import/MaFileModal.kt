package com.project.steamfarm.ui.view.modal.import

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.service.import.MaFileImport
import com.project.steamfarm.service.import.impl.DefaultMaFileImport
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.notify.NotifyView
import com.project.steamfarm.ui.view.modal.DefaultModal
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

class MaFileModal(
    private val action: (UserModel) -> Unit
): DefaultModal() {

    init {
        block.id = "maFile"
        block.layoutX = 273.0
        block.layoutY = 135.0

        val icon = ImageView().also { img ->
            img.id = "plus"
            img.layoutX = 14.0
            img.layoutY = 14.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val text = Label(langApplication.text.accounts.maFile.name).also { l ->
            l.layoutX = 46.0
            l.layoutY = 16.0
        }

        val hint = Label(langApplication.text.accounts.maFile.hint).also { l ->
            l.id = "hintMaFile"
            l.layoutX = 15.0
            l.layoutY = 264.0
        }

        block.children.addAll(icon, text, hint)
    }

    private val drag = Pane().also {
        it.id = "dragMaFile"
        it.layoutX = 28.0
        it.layoutY = 58.0

        val icon = ImageView().also { img ->
            img.id = "drag"
            img.fitWidth = 80.0
            img.fitHeight = 80.0
            img.layoutX = 60.0
            img.layoutY = 20.0
        }

        val text = Label(langApplication.text.accounts.maFile.drag).also { l ->
            l.id = "dragText"
            l.layoutX = 29.0
            l.layoutY = 100.0
        }

        it.children.addAll(icon, text)
    }

    private val file = Button(langApplication.text.accounts.maFile.file).also {
        it.id = "chooseFile"
        it.layoutX = 10.0
        it.layoutY = 154.0
    }

    private val maFileImport: MaFileImport = DefaultMaFileImport()
    private val notifyView = NotifyView()

    override fun show() {
        dragFiles()

        file.setOnMouseClicked { selectFiles() }

        drag.children.add(file)
        block.children.add(drag)

        super.show()
    }

    private fun dragFiles() {
        drag.setOnDragOver { event ->
            if (event.dragboard.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY)
            }
            event.consume()
        }
        drag.setOnDragDropped { event ->
            if (event.dragboard.hasFiles()) {
                event.isDropCompleted = true
                filterFilesAndShowPassword(event.dragboard.files)
            }
            event.consume()
        }

        drag.setOnDragEntered { event ->
            if (event.dragboard.hasFiles()) {
                drag.id = "dragMaFileActive"
            }
        }
        drag.setOnDragExited {
            drag.id = "dragMaFile"
        }
    }

    private fun selectFiles() {
        val fileChooser = FileChooser().also {
            it.title = "${langApplication.text.accounts.maFile.file} .maFile"
            it.extensionFilters.add(FileChooser.ExtensionFilter("Ma File", "*.maFile"))
        }

        val stage = root.scene.window as Stage
        val files = fileChooser.showOpenMultipleDialog(stage)
        if (files != null) {
            filterFilesAndShowPassword(files)
        }
    }

    private fun filterFilesAndShowPassword(files: List<File>) {

        val filterFiles = maFileImport.filterFiles(files)
        if (filterFiles.isEmpty()) {
            notifyView.failure(langApplication.text.failure.maFile)
        } else {
            root.children.removeIf { it.id == window.id }

            val passwordFileModal: DefaultModal = PasswordFileModal(filterFiles, action)
            passwordFileModal.show()

            notifyView.success(langApplication.text.success.maFile)
        }

    }

}