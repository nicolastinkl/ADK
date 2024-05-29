package com.vungle.ads.internal.ui

import com.vungle.ads.internal.ConfigManager
import com.vungle.ads.internal.Constants
import com.vungle.ads.internal.util.PathProvider
import java.io.File
import java.io.PrintWriter

object HackMraid {

    fun apply(pathProvider: PathProvider, out: PrintWriter) {
        val mraidJsPath = pathProvider.getJsAssetDir(ConfigManager.getMraidJsVersion())
        val mraidJsFile = File(mraidJsPath, Constants.MRAID_JS_FILE_NAME)
        if (mraidJsFile.exists()) {
            val input = mraidJsFile.bufferedReader()
            val text = input.readText()
            out.println(text)
        }
    }
}
