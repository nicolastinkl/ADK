package com.vungle.ads.internal.util

import android.os.Build
import android.util.Log
import android.webkit.URLUtil
import androidx.annotation.VisibleForTesting
import com.vungle.ads.BuildConfig
import com.vungle.ads.internal.util.FileUtility.ObjectInputStreamProvider
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.file.Files


internal object FileUtility {
    @get:VisibleForTesting
    @set:VisibleForTesting
    var objectInputStreamProvider: ObjectInputStreamProvider =
        ObjectInputStreamProvider { inputStream ->
            SafeObjectInputStream(inputStream, allowedClasses)
        }
    private val TAG = FileUtility::class.java.simpleName

    @VisibleForTesting
    internal val allowedClasses = listOf<Class<*>>(
        LinkedHashSet::class.java,
        HashSet::class.java,
        HashMap::class.java,
        ArrayList::class.java,
        File::class.java
    )

    @JvmStatic
    fun printDirectoryTree(folder: File?) {
        if (!BuildConfig.DEBUG) return
        if (folder == null) {
            Log.d(TAG, "File is null ")
            return
        }
        if (!folder.exists()) {
            Log.d(TAG, "File does not exist " + folder.path)
            return
        }
        if (!folder.isDirectory) {
            Log.d(TAG, "File is not a directory " + folder.path)
            return
        }
        val indent = 0
        val sb = StringBuilder()
        printDirectoryTree(folder, indent, sb)
        Log.d("Vungle", sb.toString())
    }

    private fun printDirectoryTree(folder: File?, indent: Int, sb: StringBuilder) {
        if (folder == null) {
            return
        }
        require(folder.isDirectory) { "folder is not a Directory" }
        sb.append(getIndentString(indent)).append("+--").append(folder.name).append("/\n")
        val files = folder.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                printDirectoryTree(file, indent + 1, sb)
            } else {
                printFile(file, indent + 1, sb)
            }
        }
    }

    private fun printFile(file: File, indent: Int, sb: StringBuilder) {
        sb.append(getIndentString(indent)).append("+--").append(file.name).append('\n')
    }

    private fun getIndentString(indent: Int): String {
        val sb = StringBuilder()
        for (i in 0 until indent) {
            sb.append("|  ")
        }
        return sb.toString()
    }

    /**
     * Helper method to recursively delete a directory. Used to clean up assets by their identifying
     * folder names.
     *
     * @param f The file or directory to delete.
     * @throws IOException if deleting a file fails for any reason.
     */
    @JvmStatic
    fun delete(f: File?) {
        if (f == null || !f.exists()) return
        if (f.isDirectory) {
            deleteContents(f)
        }
        if (!f.delete()) {
            Log.d(TAG, "Failed to delete file: $f")
        }
    }

    @JvmStatic
    fun deleteContents(folder: File) {
        val filesInFolder = folder.listFiles() ?: return
        for (fileInFolder in filesInFolder) {
            delete(fileInFolder)
        }
    }

    @JvmStatic
    fun deleteAndLogIfFailed(file: File) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.delete(file.toPath())
            } else if (!file.delete()) {
                Log.e(TAG, "Cannot delete " + file.name)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Cannot delete " + file.name, e)
        }
    }

    fun closeQuietly(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (ignored: IOException) {
            //do nothing
        }
    }

    @JvmStatic
    fun writeSerializable(
        file: File,
        serializable: Serializable?
    ) {
        if (file.exists()) deleteAndLogIfFailed(file)
        if (serializable == null) return
        var fout: FileOutputStream? = null
        var oout: ObjectOutputStream? = null
        try {
            fout = FileOutputStream(file)
            oout = ObjectOutputStream(fout)
            oout.writeObject(serializable)
            oout.reset()
        } catch (e: IOException) {
            Log.e(TAG, "IOIOException", e)
        } finally {
            closeQuietly(oout)
            closeQuietly(fout)
        }
    }

    @JvmStatic
    fun <T> readSerializable(file: File): T? {
        if (!file.exists()) return null
        var fin: FileInputStream? = null
        var oin: ObjectInputStream? = null
        try {
            fin = FileInputStream(file)
            oin = objectInputStreamProvider.provideObjectInputStream(fin)
            return oin.readObject() as T
        } catch (e: IOException) {
            Log.e(TAG, "IOIOException", e)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "ClassNotFoundException", e)
        } catch (e: Exception) {
            //TODO after implementing logging send this corrupted file to us for investigation
            Log.e(TAG, "cannot read serializable", e)
        } finally {
            closeQuietly(oin)
            closeQuietly(fin)
        }
        try {
            delete(file)
        } catch (ignored: IOException) {
            //ignored
        }
        return null
    }

    fun isValidUrl(httpUrl: String?): Boolean {
        return !httpUrl.isNullOrEmpty() && httpUrl.toHttpUrlOrNull() != null
    }

    fun size(file: File?): Long {
        if (file == null || !file.exists()) return 0
        var length: Long = 0
        if (file.isDirectory) {
            val children = file.listFiles()
            if (children != null && children.isNotEmpty()) {
                for (child in children) {
                    length += size(child)
                }
            }
            return length
        }
        return file.length()
    }

    fun guessFileName(url: String, ext: String?): String {
        return URLUtil.guessFileName(url, null, ext)
    }

    internal fun interface ObjectInputStreamProvider {
        @Throws(IOException::class, ClassNotFoundException::class)
        fun provideObjectInputStream(inputStream: InputStream?): ObjectInputStream
    }
}
