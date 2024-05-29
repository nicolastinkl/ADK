package com.vungle.ads.internal.omsdk

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread
import com.iab.omid.library.vungle.Omid
import com.vungle.ads.internal.util.FileUtility.closeQuietly
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicReference

class OMInjector(context: Context) {
    private val uiHandler = Handler(Looper.getMainLooper())
    private val contextRef: AtomicReference<Context>
    fun init() {
        uiHandler.post {
            try {
                if (!Omid.isActive()) {
                    Omid.activate(contextRef.get())
                }
            } catch (error: NoClassDefFoundError) {
                Log.e("OMSDK", "error: ${error.localizedMessage}")
            }
        }
    }

    @WorkerThread
    @Throws(IOException::class)
    fun injectJsFiles(dir: File): List<File> {
        val list = ArrayList<File>()
        list.add(writeToFile(Res.OM_JS, File(dir, OM_SDK_JS)))
        list.add(writeToFile(Res.OM_SESSION_JS, File(dir, OM_SESSION_JS)))
        return list
    }

    @Throws(IOException::class)
    private fun writeToFile(lines: String, outputFile: File): File {
        var writer: FileWriter? = null
        try {
            writer = FileWriter(outputFile)
            writer.write(lines)
            writer.flush()
        } finally {
            closeQuietly(writer)
        }
        return outputFile
    }

    companion object {
        private const val OM_SDK_JS = "omsdk.js"
        private const val OM_SESSION_JS = "omsdk-session.js"
    }

    init {
        contextRef = AtomicReference(context.applicationContext)
    }
}