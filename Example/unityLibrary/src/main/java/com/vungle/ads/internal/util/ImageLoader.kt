package com.vungle.ads.internal.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import java.util.concurrent.Executor

/**
 * ImageLoader for native ad assets and only for already cached local files currently.
 */
class ImageLoader private constructor() {
    private val lruCache: LruCache<String?, Bitmap>
    private var ioExecutor: Executor? = null

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8
        lruCache = object : LruCache<String?, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    fun init(ioExecutor: Executor) {
        this.ioExecutor = ioExecutor
    }

    fun displayImage(uri: String?, onImageLoaded: (bitmap: Bitmap) -> Unit) {
        if (ioExecutor == null) {
            Log.w(TAG, "ImageLoader not initialized.")
            return
        }
        if (uri.isNullOrEmpty()) {
            Log.w(TAG, "the uri is required.")
            return
        }
        ioExecutor?.execute(Runnable {
            if (!uri.startsWith(FILE_SCHEME)) {
                return@Runnable
            }
            val cachedBitmap = lruCache[uri]
            if (cachedBitmap != null && !cachedBitmap.isRecycled) {
                onImageLoaded(cachedBitmap)
                return@Runnable
            }
            val assetFile = uri.substring(FILE_SCHEME.length)
            val bitmap = BitmapFactory.decodeFile(assetFile)
            if (bitmap != null) {
                lruCache.put(uri, bitmap)
                onImageLoaded(bitmap)
            } else {
                Log.w(TAG, "decode bitmap failed.")
            }
        })
    }

    companion object {
        private val TAG = ImageLoader::class.java.simpleName
        private const val FILE_SCHEME = "file://"
        val instance = ImageLoader()
    }
}
