package com.vungle.ads.internal.executor

import android.util.Log
import com.vungle.ads.BuildConfig
import java.lang.RuntimeException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FutureResult<T>(val future: Future<T>?) : Future<T?> {
    companion object {
        val TAG = FutureResult::class.java.simpleName
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        return future?.cancel(mayInterruptIfRunning) ?: false
    }

    override fun isCancelled(): Boolean {
        return future?.isCancelled ?: false
    }

    override fun isDone(): Boolean {
        return future?.isDone ?: false
    }

    override fun get(): T? {
        var result: T? = null
        try {
            result = future?.get()
        } catch (e: InterruptedException) {
            Log.w(TAG, "future.get() Interrupted on Thread " + Thread.currentThread().name)
            Thread.currentThread().interrupt()
        } catch (e: ExecutionException) {
            Log.e(TAG, "error on execution", e)
            if (BuildConfig.DEBUG) throw RuntimeException(e)
        }
        return result
    }

    override fun get(timeout: Long, unit: TimeUnit): T? {
        var result: T? = null
        try {
            result = future?.get(timeout, unit)
        } catch (e: InterruptedException) {
            Log.w(TAG, "future.get() Interrupted on Thread " + Thread.currentThread().name)
            Thread.currentThread().interrupt()
        } catch (e: ExecutionException) {
            Log.e(TAG, "error on execution", e)
            if (BuildConfig.DEBUG) throw RuntimeException(e)
        } catch (e: TimeoutException) {
            if (BuildConfig.DEBUG) Log.e(TAG, "error on timeout", e)
            Log.w(TAG, "future.get() Timeout on Thread " + Thread.currentThread().name)
        }
        return result
    }
}