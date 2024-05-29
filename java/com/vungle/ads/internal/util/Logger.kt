@file:JvmName("Logger")
package com.vungle.ads.internal.util

import android.util.Log

/**
 * Use this class to do all Logging instead of calling Log.d, Log.e or Log.w
 */
class Logger {

    companion object {
        private var enabled : Boolean = false

        fun enable(enabled : Boolean) {
            Companion.enabled = enabled
        }

        @JvmStatic
        fun d(tag : String?, message : String?) : Int {
            if (enabled && tag != null && message != null) {
                return Log.d(tag, message)
            }
            return -1
        }

        @JvmStatic
        fun e(tag : String?, message : String?) : Int  {
            if (enabled && tag != null && message != null){
                return Log.e(tag, message)
            }
            return -1
        }

        @JvmStatic
        fun e(tag : String?, message : String?, throwable: Throwable?) : Int {
            if (enabled && tag != null && message != null) {
                return Log.e(tag, message, throwable)
            }
            return -1
        }

        @JvmStatic
        fun w(tag : String?, message : String?) : Int  {
            if (enabled && tag != null && message != null) {
                return Log.w(tag, message)
            }
            return -1
        }

        @JvmStatic
        fun w(tag : String?, message : String?, throwable: Throwable?) : Int {
            if (enabled && message != null && throwable != null) {
                Log.w(tag, message, throwable)
            }
            return -1
        }
    }

}