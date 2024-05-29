package com.vungle.ads.internal.task

abstract class PriorityRunnable : Comparable<Any>, Runnable {
    //Higher priority goes first
    override operator fun compareTo(other: Any): Int {
        if (other is PriorityRunnable) {
            val current = priority
            val incoming = other.priority
            return incoming.compareTo(current)
        }
        return -1
    }

    abstract val priority: Int
}