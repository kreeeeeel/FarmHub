package com.project.panel.data

import com.project.panel.model.UserModel
import com.project.panel.service.process.ProcessType
import com.sun.jna.platform.win32.WinDef.HWND

data class LobbyUserData(
    var userModel: UserModel,
    var userHWND: HWND,
    var pids: Map<ProcessType, List<Int>>
) {
    companion object {
        fun mapper(userModel: UserModel) = LobbyUserData(userModel, HWND(), emptyMap())
    }
}
