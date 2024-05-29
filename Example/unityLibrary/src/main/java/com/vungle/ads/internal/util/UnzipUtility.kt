package com.vungle.ads.internal.util

import android.util.Log
import androidx.annotation.VisibleForTesting
import java.io.*
import java.util.ArrayList
import java.util.zip.ZipFile

/**
 * Utility
 */
object UnzipUtility {
    /**
     * Size of the buffer to read/write data
     */
    private const val BUFFER_SIZE = 4096
    private val TAG = UnzipUtility::class.java.canonicalName
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     *
     * @param zipFilePath   path of [File] to unzip
     * @param destDirectory destination for extracted files
     * @param filter        [Filter] to match extracted files against. Only mathching ones get
     * into `destDirectory`
     * @throws IOException if operation cannot be completed successfully
     */
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     *
     * @param zipFilePath   path of [File] to unzip
     * @param destDirectory destination for extracted files
     * @throws IOException if operation cannot be completed successfully
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun unzip(zipFilePath: String?, destDirectory: String, filter: Filter? = null): List<File> {
        if (zipFilePath.isNullOrEmpty()) throw IOException("Path is empty")
        val src = File(zipFilePath)
        if (!src.exists()) {
            throw IOException("File does not exist")
        }
        val destDir = File(destDirectory)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        var zipFile: ZipFile? = null
        val extractedFiles: MutableList<File> = ArrayList()
        try {
            zipFile = ZipFile(src)
            val entries = zipFile.entries()

            // iterates over entries in the zip file
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val filePath = destDirectory + File.separator + entry.name
                if (filter == null || filter.matches(filePath)) {
                    validateFilename(filePath, destDirectory)
                    if (entry.isDirectory) {
                        // if the entry is a directory, make the directory if it does not exist.
                        val dir = File(filePath)
                        if (!dir.exists()) {
                            dir.mkdirs()
                        }
                    } else {
                        // if the entry is a file, extracts it
                        extractFile(zipFile.getInputStream(entry), filePath)
                        extractedFiles.add(File(filePath))
                    }
                }
            }
        } finally {
            try {
                zipFile?.close()
            } catch (e: IOException) {
                //ignored
            }
        }
        return extractedFiles
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn    [InputStream] on ZIP entry
     * @param filePath extraction destination
     * @throws IOException if it is impossible to finish operation properly
     */
    @VisibleForTesting
    @Throws(IOException::class)
    fun extractFile(zipIn: InputStream, filePath: String?) {
        val dest = File(filePath)
        FileUtility.delete(dest)
        val parentDir = dest.parentFile
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs()
        }
        var bos: BufferedOutputStream? = null
        var fos: FileOutputStream? = null
        try {
            bos = BufferedOutputStream(FileOutputStream(filePath).also { fos = it })
            val bytesIn = ByteArray(BUFFER_SIZE)
            var read: Int
            while (zipIn.read(bytesIn).also { read = it } != -1) {
                bos.write(bytesIn, 0, read)
            }
        } finally {
            FileUtility.closeQuietly(zipIn)
            FileUtility.closeQuietly(bos)
            FileUtility.closeQuietly(fos)
        }
    }

    @Throws(IOException::class)
    private fun validateFilename(filename: String, intendedDir: String): String {
        val f = File(filename)
        val canonicalPath = f.canonicalPath
        val iD = File(intendedDir)
        val canonicalID = iD.canonicalPath
        return if (canonicalPath.startsWith(canonicalID)) {
            canonicalPath
        } else {
            val msg = "File is outside extraction target directory."
            Log.e(TAG, msg)
            throw ZipSecurityException(msg)
        }
    }

    internal class ZipSecurityException(message: String?) : IOException(message)
    interface Filter {
        fun matches(extractPath: String?): Boolean
    }
}