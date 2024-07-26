package com.project.panel.ui.view.modal.import

import com.project.panel.langApplication
import com.project.panel.model.ConfigModel
import com.project.panel.ui.view.modal.DefaultModal
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import java.io.File

const val MAFILE_MODAL_TYPE = "mafile"
const val PASSWORD_MODAL_TYPE = "password"

abstract class ImportModal(
    private val modalType: String
): DefaultModal() {

    protected val configModel = ConfigModel().fromFile()

    init {
        block.id = "importFile"
        block.layoutX = 273.0
        block.layoutY = 135.0

        val icon = ImageView().also { img ->
            img.id = if (modalType == MAFILE_MODAL_TYPE) "plus" else "password"
            img.layoutX = 14.0
            img.layoutY = 14.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val text = Label().also { l ->
            l.text = if (modalType == MAFILE_MODAL_TYPE) langApplication.text.accounts.maFile.name
            else langApplication.text.accounts.passwordFile.name
            l.layoutX = 46.0
            l.layoutY = 16.0
        }

        val hint = Label().also { l ->
            l.text = if (modalType == MAFILE_MODAL_TYPE) langApplication.text.accounts.maFile.hint
            else langApplication.text.accounts.passwordFile.hint
            l.id = if (modalType == MAFILE_MODAL_TYPE) "hintMaFile" else "passwordHintFile"
            l.layoutX = if (modalType == MAFILE_MODAL_TYPE) 15.0 else 13.0
            l.layoutY = if (modalType == MAFILE_MODAL_TYPE) 264.0 else 258.0
        }

        val drag = Pane().also {
            it.id = "dragFile"
            it.layoutX = 28.0
            it.layoutY = 58.0

            ImageView().also { img ->
                img.id = "drag"
                img.fitWidth = 80.0
                img.fitHeight = 80.0
                img.layoutX = 60.0
                img.layoutY = 20.0

                it.children.add(img)
            }

            Label(langApplication.text.accounts.maFile.drag).also { l ->
                l.id = "dragText"
                l.layoutX = 29.0
                l.layoutY = 100.0

                it.children.add(l)
            }

            it.setOnDragOver { event ->
                if (event.dragboard.hasFiles()) event.acceptTransferModes(TransferMode.COPY)
                event.consume()
            }
            it.setOnDragDropped { event ->
                if (event.dragboard.hasFiles()) {
                    event.isDropCompleted = true
                    handler(event.dragboard.files)
                }
                event.consume()
            }
            it.setOnDragEntered { event -> if (event.dragboard.hasFiles()) it.id = "dragFileActive" }
            it.setOnDragExited { _ -> it.id = "dragFile" }
        }

        val file = Button(langApplication.text.accounts.maFile.file).also {
            it.id = "chooseFile"
            it.layoutX = 10.0
            it.layoutY = 154.0

            it.setOnMouseClicked { _ -> fileChooser() }
        }

        drag.children.add(file)
        block.children.addAll(icon, text, hint, drag)

    }

    abstract fun fileChooser(): Unit?
    abstract fun handler(files: List<File>)

}