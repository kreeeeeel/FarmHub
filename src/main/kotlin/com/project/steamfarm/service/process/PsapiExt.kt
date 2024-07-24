package com.project.steamfarm.service.process

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions

@Suppress("FunctionName")
interface PsapiExt : StdCallLibrary {
    fun EnumProcesses(lpidProcess: IntArray, cb: Int, lpcbNeeded: IntByReference): Boolean
    fun GetProcessImageFileNameA(hProcess: Pointer, lpImageFileName: ByteArray, nSize: Int): Int

    companion object {
        val INSTANCE: PsapiExt = Native.load("psapi", PsapiExt::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }
}