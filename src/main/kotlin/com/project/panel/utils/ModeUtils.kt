package com.project.panel.utils

import com.project.panel.langApplication
import com.project.panel.ui.view.section.CS_NAME
import com.project.panel.ui.view.section.DOTA_NAME

object ModeUtils {

    fun getEnabledMode(isDotaEnabled: Boolean, isCsEnabled: Boolean): String {
        if (!isDotaEnabled && !isCsEnabled) return langApplication.text.accounts.userNotActive

        val stringBuilder = StringBuilder()
        if (isDotaEnabled) stringBuilder.append(DOTA_NAME)
        if (isCsEnabled) {
            if (stringBuilder.isNotEmpty()) stringBuilder.append(" | ")
            stringBuilder.append(CS_NAME)
        }
        return stringBuilder.toString()
    }

}