package com.vungle.ads.internal.util

import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectStreamClass

internal class SafeObjectInputStream(`in`: InputStream?, private val allowed: List<Class<*>>?) :
    ObjectInputStream(`in`) {
    @Throws(ClassNotFoundException::class, IOException::class)
    override fun resolveClass(desc: ObjectStreamClass): Class<*> {
        val c = super.resolveClass(desc)
        if (allowed == null || Number::class.java.isAssignableFrom(c)
            || String::class.java == c || Boolean::class.javaObjectType == c || c.isArray
            || allowed.contains(c)
        ) {
            return c
        }
        throw IOException("Deserialization is not allowed for " + desc.name)
    }
}