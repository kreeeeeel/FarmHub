package com.project.steamfarm.ui.view.block.account

import com.project.steamfarm.langApplication
import com.project.steamfarm.model.UserModel
import com.project.steamfarm.ui.view.section.CS_NAME
import com.project.steamfarm.ui.view.section.DOTA_NAME
import javafx.scene.layout.Pane

class AccountView {

    //fun getUserView(userModel: UserModel): Pane

}

fun getStatusGame(isDotaEnabled: Boolean, isCsEnabled: Boolean): String {
    if (!isDotaEnabled && !isCsEnabled) {
        return langApplication.text.accounts.unused
    }

    val stringBuilder = StringBuilder()
    if (isDotaEnabled) stringBuilder.append(DOTA_NAME)
    if (isCsEnabled) {
        if (stringBuilder.isNotEmpty()) stringBuilder.append(" | ")
        stringBuilder.append(CS_NAME)
    }
    return stringBuilder.toString()
}