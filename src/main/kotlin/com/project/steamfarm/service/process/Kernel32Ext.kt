package com.project.steamfarm.service.process

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions

@Suppress("FunctionName")
interface Kernel32Ext: StdCallLibrary {

    fun OpenProcess(dwDesiredAccess: Int, bInheritHandle: Boolean, dwProcessId: Int): Pointer?
    fun CloseHandle(hObject: Pointer): Boolean

    companion object {
        const val PROCESS_QUERY_INFORMATION = 0x0400
        const val PROCESS_VM_READ = 0x0010
        val INSTANCE: Kernel32Ext = Native.load("kernel32", Kernel32Ext::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }

}