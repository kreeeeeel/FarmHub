package com.project.panel.ui.view.notify

import com.project.panel.ui.controller.BaseController.Companion.root
import javafx.animation.FadeTransition
import javafx.animation.PauseTransition
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration
import java.time.OffsetTime
import java.util.concurrent.CompletableFuture

const val MAX_COUNT_VIEW = 3

object NotifyView {

    private val queue: MutableList<NotifyQueue> = mutableListOf()
    private val currentNotifyOnScene: MutableList<Pane> = mutableListOf()

    fun success(value: String) = notify(value, NotifyType.SUCCESS)
    fun failure(value: String) = notify(value, NotifyType.FAILURE)
    fun warning(value: String) = notify(value, NotifyType.WARNING)

    private fun notify(text: String, type: NotifyType) {
        if (currentNotifyOnScene.size < MAX_COUNT_VIEW) {
            val createNotify = createNotify(text, type)

            var sumHeight = currentNotifyOnScene.sumOf { it.prefHeight }
            if (sumHeight > 0) sumHeight += currentNotifyOnScene.size * 5.0
            val layoutY = root.scene.height - sumHeight - 65.0

            createNotify.layoutY = layoutY
            currentNotifyOnScene.add(createNotify)
            root.children.addAll(createNotify)

            alwaysToFront(createNotify)
            showAnimation(createNotify)
        } else queue.add(NotifyQueue(text, type))
    }

    private fun createNotify(text: String, type: NotifyType): Pane {
        val notify = Pane().also {
            it.id = when(type) {
                NotifyType.SUCCESS -> "notifySuccess"
                NotifyType.WARNING -> "notifyWarning"
                NotifyType.FAILURE -> "notifyFailure"
            }
        }

        val icon = ImageView().also {
            it.id = when(type) {
                NotifyType.SUCCESS -> "success"
                else -> "error"
            }
            it.fitWidth = 36.0
            it.fitHeight = it.fitWidth
            it.layoutY = 7.0
            it.layoutX = 7.0
        }

        val textFX = Text(text).also {
            it.font = Font.font("Franklin Gothic Medium", 14.0)
            if (it.layoutBounds.width > 370) {
                it.wrappingWidth = 370.0
            }
        }

        val label = Label(text).also {
            it.id = "notifyDesc"
            it.layoutX = 52.0
            it.layoutY = 7.0
            it.prefWidth = textFX.layoutBounds.width
            it.prefHeight = textFX.layoutBounds.height
        }

        notify.prefWidth = icon.layoutX + icon.fitHeight + label.layoutX + label.prefWidth
        notify.prefHeight = maxOf(icon.fitHeight + 14.0, label.layoutBounds.height + 14.0)
        notify.layoutX = root.scene.width - notify.prefWidth - 20.0

        val offsetTime = OffsetTime.now()
        val timeFormat = String.format("%02d:%02d", offsetTime.hour, offsetTime.minute)
        val time = Label(timeFormat).also {
            it.id = "notifyTime"
            it.layoutX = notify.prefWidth - 50.0
            it.layoutY = notify.prefHeight - 25.0
        }

        val clear = Pane().also { p ->
            p.layoutX = notify.prefWidth - 30.0
            p.layoutY = -12.0
            p.prefWidth = 32.0
            p.prefHeight = 32.0

            val clearImg = ImageView().also { img ->
                img.id = "clear"
                img.fitWidth = p.prefWidth
                img.fitHeight = p.prefHeight
            }

            p.setOnMouseClicked { event ->
                deleteNotify(notify)
                event.consume()
            }
            p.children.add(clearImg)
        }

        notify.children.addAll(icon, label, time, clear)
        return notify
    }

    private fun deleteNotify(currentNotify: Pane) {
        if (!currentNotifyOnScene.contains(currentNotify)) return

        currentNotify.opacity = 0.0
        currentNotifyOnScene.remove(currentNotify)
        root.children.remove(currentNotify)

        var offsetY = 0.0
        currentNotifyOnScene.forEach {
            it.layoutY = root.scene.height - offsetY - 65.0
            offsetY += it.prefHeight + 5.0
        }

        if (queue.isNotEmpty()) {
            val notifyFromQueue = queue.first()
            queue.remove(notifyFromQueue)

            notify(notifyFromQueue.text, notifyFromQueue.type)
        }
    }

    private fun showAnimation(notify: Pane) = TranslateTransition(Duration.millis(430.0)).also {
        it.node = notify
        it.fromX = 1000.0
        it.toX = 0.0
        it.cycleCount = 1
        it.isAutoReverse = true
        it.playFromStart()

        it.setOnFinished { pauseAnimation(notify) }
    }

    private fun pauseAnimation(notify: Pane) = PauseTransition(Duration.seconds(5.0)).also {
        it.playFromStart()
        it.setOnFinished { fadeAnimation(notify) }
    }

    private fun fadeAnimation(notify: Pane) = FadeTransition(Duration.millis(530.0)).also {
        it.node = notify
        it.fromValue = 1.0
        it.toValue = 0.0

        it.playFromStart()
        it.setOnFinished { deleteNotify(notify) }
    }

    private fun alwaysToFront(notify: Pane) = CompletableFuture.supplyAsync {
        while (root.children.contains(notify)) {
            Platform.runLater { notify.toFront() }
            Thread.sleep(500)
        }
    }

}

private enum class NotifyType { SUCCESS, WARNING, FAILURE }
private data class NotifyQueue(val text: String, val type: NotifyType)