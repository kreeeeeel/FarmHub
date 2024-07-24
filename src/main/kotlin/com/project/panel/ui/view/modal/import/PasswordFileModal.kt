package com.project.panel.ui.view.modal.import

import com.project.panel.langApplication
import com.project.panel.model.UserModel
import com.project.panel.repository.impl.UserRepository
import com.project.panel.service.import.PasswordImport
import com.project.panel.service.import.impl.DefaultPasswordImport
import com.project.panel.service.steam.ClientSteam
import com.project.panel.service.steam.impl.DefaultClientSteam
import com.project.panel.ui.controller.BaseController.Companion.root
import com.project.panel.ui.view.modal.DefaultModal
import com.project.panel.ui.view.notify.NotifyView
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.CompletableFuture

class PasswordFileModal(
    private val maFiles: List<File>,
    private val action: (UserModel) -> Unit
): DefaultModal() {

    init {
        block.id = "passwordFile"
        block.layoutX = 273.0
        block.layoutY = 135.0

        val icon = ImageView().also { img ->
            img.id = "password"
            img.layoutX = 14.0
            img.layoutY = 14.0
            img.fitWidth = 24.0
            img.fitHeight = 24.0
        }

        val text = Label(langApplication.text.accounts.passwordFile.name).also { l ->
            l.layoutX = 46.0
            l.layoutY = 16.0
        }

        val hint = Label(langApplication.text.accounts.passwordFile.hint).also { l ->
            l.id = "passwordHintFile"
            l.layoutX = 13.0
            l.layoutY = 258.0
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

    private val notifyView = NotifyView()

    private val passwordImport: PasswordImport = DefaultPasswordImport()
    private val clientSteam: ClientSteam = DefaultClientSteam()

    override fun show() {
        dragFiles()

        file.setOnMouseClicked { runBlocking { selectFile() } }

        drag.children.addAll(file)
        block.children.addAll(drag)

        super.show(false)
    }

    private fun dragFiles() {
        drag.setOnDragOver { event ->
            if (event.dragboard.hasFiles()) event.acceptTransferModes(TransferMode.COPY)
            event.consume()
        }
        drag.setOnDragDropped { event ->
            if (event.dragboard.hasFiles()) {
                event.isDropCompleted = true
                filterFileAndAuth(event.dragboard.files[0])
            }
            event.consume()
        }
        drag.setOnDragEntered { event -> if (event.dragboard.hasFiles()) drag.id = "dragMaFileActive" }
        drag.setOnDragExited { drag.id = "dragMaFile" }
    }

    private fun selectFile() = FileChooser().also {
        it.title = "${langApplication.text.accounts.maFile.file} .txt"
        it.extensionFilters.add(FileChooser.ExtensionFilter("Txt File", "*.txt"))
    }.showOpenDialog(root.scene.window as Stage)?.let { filterFileAndAuth(it) }

    private fun filterFileAndAuth(file: File) = passwordImport.getPasswordsFromFile(file, maFiles).let {
        if (it.isNotEmpty()) {
            root.children.remove(window)
            authenticate(it.toMutableList())
            when (it.size != maFiles.size) {
                true -> notifyView.warning(langApplication.text.warning.notAllAccount)
                else -> notifyView.success(langApplication.text.success.import)
            }
        } else notifyView.failure(langApplication.text.failure.passwordsNotFound)
    }

    private fun authenticate(userModels: MutableList<UserModel>) = CompletableFuture.supplyAsync {
        userModels.mapNotNull { it.steam.session }
            .mapNotNull { it.steamID }
            .map { CompletableFuture.supplyAsync { clientSteam.getProfileData(it) } }
            .forEachIndexed { index, futureData ->
                futureData.thenAccept { data ->
                    val currentUserModel = userModels[index]
                    currentUserModel.createdTs = System.currentTimeMillis()
                    data?.let { currentUserModel.photo = it.avatar }

                    UserRepository.save(currentUserModel)
                    Platform.runLater { action.invoke(currentUserModel) }
                }
            }
    }
}
