package com.project.steamfarm.ui.view.window.import

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.import.PasswordImport
import com.project.steamfarm.service.import.impl.DefaultPasswordImport
import com.project.steamfarm.service.steam.ClientSteam
import com.project.steamfarm.service.steam.impl.DefaultClientSteam
import com.project.steamfarm.ui.controller.BaseController.Companion.root
import com.project.steamfarm.ui.view.notify.NotifyView
import com.project.steamfarm.ui.view.window.DefaultWindow
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.util.concurrent.CompletableFuture

class PasswordFileWindow(
    private val maFiles: List<File>,
    private val action: (UserModel) -> Unit
): DefaultWindow() {

    private val block = Pane().also {
        it.id = "passwordFile"
        it.layoutX = 273.0
        it.layoutY = 135.0

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

        it.children.addAll(icon, text, hint)
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

        file.setOnMouseClicked { selectFile() }

        drag.children.addAll(file)
        block.children.addAll(drag)
        window.children.addAll(block)

        super.show(false)
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
                filterFileAndAuth(event.dragboard.files[0])
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

    private fun selectFile() {

        val fileChooser = FileChooser().also {
            it.title = "${langApplication.text.accounts.maFile.file} .txt"
            it.extensionFilters.add(FileChooser.ExtensionFilter("Txt File", "*.txt"))
        }

        val stage = root.scene.window as Stage
        val file = fileChooser.showOpenDialog(stage)
        if (file != null) {
            filterFileAndAuth(file)
        }

    }

    private fun filterFileAndAuth(file: File) {

        val userModels = passwordImport.getPasswordsFromFile(file, maFiles)
        if (userModels.isEmpty()) {
            notifyView.failure(langApplication.text.failure.passwordsNotFound)

        } else {
            authenticate(userModels)
            root.children.remove(window)

            if (userModels.size != maFiles.size) {
                notifyView.warning(langApplication.text.warning.notAllAccount)
            } else notifyView.success(langApplication.text.success.import)
        }
    }

    private fun authenticate(userModels: List<UserModel>) = userModels.forEach {
        CompletableFuture.supplyAsync {
            it.createdTs = System.currentTimeMillis()
            if (clientSteam.authentication(it.username, it.password)) {

                val data = clientSteam.getProfileData()
                if (data != null) {
                    it.photo = data.avatar
                }

                UserRepository.save(it)
                Platform.runLater { action.invoke(it) }
            }
        }
    }

}