package com.vungle.ads.internal.persistence

import androidx.annotation.VisibleForTesting
import com.vungle.ads.internal.util.CollectionsConcurrencyUtil
import com.vungle.ads.internal.util.FileUtility
import com.vungle.ads.internal.util.PathProvider
import java.io.File
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor

class FilePreferences constructor(
    private val ioExecutor: Executor,
    pathProvider : PathProvider,
    filename: String = FILENAME,
) {
    private val file: File = File(pathProvider.getSharedPrefsDir(), filename)
    private val values = ConcurrentHashMap<String, Any>()

    fun apply() {
        val serializable: Serializable = HashMap(values)
        ioExecutor.execute { FileUtility.writeSerializable(file, serializable) }
    }

    fun put(key: String, value: Boolean): FilePreferences {
        values[key] = value
        return this
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val value = values[key]
        return if (value is Boolean) value else defaultValue
    }

    fun getBoolean(key: String): Boolean? {
        val value = values[key]
        return if (value is Boolean) value else null
    }

    fun put(key: String, value: String): FilePreferences {
        values[key] = value
        return this
    }

    fun remove(key: String): FilePreferences {
        if (values.containsKey(key)) {
            values.remove(key)
        }
        return this
    }

    fun getString(key: String, defaultValue: String): String {
        val value = values[key]
        return if (value is String) value else defaultValue
    }

    fun getString(key: String): String? {
        val value = values[key]
        return if (value is String) value else null
    }

    fun put(key: String, value: Int): FilePreferences {
        values[key] = value
        return this
    }

    fun getInt(key: String, defaultValue: Int): Int {
        val value = values[key]
        return if (value is Int) value else defaultValue
    }

    fun put(key: String, value: HashSet<String>?): FilePreferences {
        values[key] = CollectionsConcurrencyUtil.getNewHashSet(value)
        return this
    }

    fun getStringSet(key: String, defaultValue: HashSet<String>): HashSet<String> {
        val value = values[key]
        return if (value is HashSet<*>) CollectionsConcurrencyUtil.getNewHashSet(value as HashSet<String>?) else defaultValue
    }

    fun put(key: String, value: Long): FilePreferences {
        values[key] = value
        return this
    }

    fun getLong(key: String, defaultValue: Long): Long {
        val value = values[key]
        return if (value is Long) value else defaultValue
    }

    companion object {
        @VisibleForTesting
        val FILENAME = "settings_vungle"
    }

    init {
        val saved = FileUtility.readSerializable<Any>(file)
        if (saved is HashMap<*, *>) {
            values.putAll(saved as HashMap<String, *>)
        }
    }
}
