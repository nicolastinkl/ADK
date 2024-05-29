package com.vungle.ads.internal.util

import java.util.HashSet

object CollectionsConcurrencyUtil {
    @Synchronized
    @JvmStatic
    fun getNewHashSet(hashSet: HashSet<String>?): HashSet<String> {
        return HashSet(hashSet)
    }

    @Synchronized
    @JvmStatic
    fun addToSet(hashset: HashSet<String>, set: String) {
        hashset.add(set)
    }
}