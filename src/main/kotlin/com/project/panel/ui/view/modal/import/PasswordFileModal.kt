package com.project.panel.ui.view.modal.import

import com.project.panel.langApplication
import com.project.panel.model.UserModel
import com.project.panel.repository.impl.UserRepository
import com.project.panel.service.import.PasswordImport
import com.project.panel.service.import.impl.DefaultPasswordImport
import com.project.panel.service.logger.LoggerService
import com.project.panel.service.steam.ClientSteam
import com.project.panel.service.steam.impl.DefaultClientSteam
import com.project.panel.ui.controller.BaseController.Companion.root
import com.project.panel.ui.view.notify.NotifyView
import javafx.application.Platform
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.util.concurrent.CompletableFuture

class PasswordFileModal(
    private val maFiles: List<File>,
    private val action: (UserModel) -> Unit
): ImportModal(PASSWORD_MODAL_TYPE) {

    private val notifyView = NotifyView()

    private val passwordImport: PasswordImport = DefaultPasswordImport()
    private val clientSteam: ClientSteam = DefaultClientSteam()

    override fun show() {
        super.show(false)
    }

    override fun fileChooser() = FileChooser().also {
        it.title = "${langApplication.text.accounts.maFile.file} .txt"
        it.extensionFilters.add(FileChooser.ExtensionFilter("Txt File", "*.txt"))

        configModel.lastDirectoryChooser?.let { dir ->
            val file = File(dir)
            if (file.exists()) it.initialDirectory = File(dir)
        }
    }.showOpenDialog(root.scene.window as Stage)?.let { handler(listOf(it)) }

    override fun handler(files: List<File>) {
        LoggerService.getLogger().info("Receiving a file with passwords: ${files[0].absolutePath}")
        passwordImport.getPasswordsFromFile(files[0], maFiles).let {

            configModel.lastDirectoryChooser = files[0].parentFile.absolutePath
            configModel.save()

            if (it.isNotEmpty()) {
                root.children.remove(window)
                authenticate(it.toMutableList())
                when (it.size != maFiles.size) {
                    true -> {
                        LoggerService.getLogger().warning("Not all account passwords have been found!")
                        notifyView.warning(langApplication.text.warning.notAllAccount)
                    }

                    else -> {
                        LoggerService.getLogger().info("Passwords for all accounts have been found!")
                        notifyView.success(langApplication.text.success.import)
                    }
                }
            } else {
                LoggerService.getLogger()
                    .error("The file turned out to be invalid or does not contain account passwords")
                notifyView.failure(langApplication.text.failure.passwordsNotFound)
            }
        }
    }

    private fun authenticate(userModels: MutableList<UserModel>) = CompletableFuture.supplyAsync {
        userModels.mapNotNull { it.steam.session }
            .mapNotNull { it.steamID }
            .map {
                CompletableFuture.supplyAsync {
                    LoggerService.getLogger().info("Request get info for steamId:$it")
                    clientSteam.getProfileData(it)
                }
            }
            .forEachIndexed { index, futureData ->
                futureData.thenAccept { data ->
                    val currentUserModel = userModels[index]
                    currentUserModel.createdTs = System.currentTimeMillis()
                    data?.let { currentUserModel.photo = it.avatar }

                    LoggerService.getLogger().info("Completed request for ${currentUserModel.steam.session!!.steamID}")
                    UserRepository.save(currentUserModel)
                    Platform.runLater { action.invoke(currentUserModel) }
                }
            }
    }
}
