package com.vungle.ads

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.annotation.Keep
import com.vungle.ads.internal.bidding.BidTokenEncoder
import com.vungle.ads.internal.downloader.AssetDownloader
import com.vungle.ads.internal.downloader.Downloader
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.executor.SDKExecutors
import com.vungle.ads.internal.locale.LocaleInfo
import com.vungle.ads.internal.locale.SystemLocaleInfo
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.omsdk.OMInjector
import com.vungle.ads.internal.omsdk.OMTracker
import com.vungle.ads.internal.persistence.FilePreferences
import com.vungle.ads.internal.platform.AndroidPlatform
import com.vungle.ads.internal.platform.Platform
import com.vungle.ads.internal.task.*
import com.vungle.ads.internal.util.ConcurrencyTimeoutProvider
import com.vungle.ads.internal.util.PathProvider

internal class ServiceLocator private constructor(context: Context) {
    private val ctx: Context = context.applicationContext
    private val creators: MutableMap<Class<*>, Creator<*>> = HashMap()
    private val cache: MutableMap<Class<*>, Any?> = HashMap()

    init {
        buildCreators()
    }

    @Synchronized
    fun <T> getService(serviceClass: Class<T>): T {
        return getOrBuild(serviceClass)
    }

    @Synchronized
    fun <T> isCreated(serviceClass: Class<T>): Boolean {
        return cache.containsKey(getServiceClass(serviceClass))
    }

    @VisibleForTesting
    internal fun <T> getOrBuild(serviceClass: Class<T>): T {
        val target = getServiceClass(serviceClass)
        var value = cache[target]
        if (value == null) {
            val creator = creators[target]
                ?: throw IllegalArgumentException("Unknown class")
            value = creator.create()
            if (creator.isSingleton) cache[target] = value
        }
        return value as T
    }

    @VisibleForTesting
    internal fun <T> bindService(serviceClass: Class<*>, service: T) {
        cache[serviceClass] = service
    }

    private fun getServiceClass(serviceClass: Class<*>): Class<*> {
        for (clazz in creators.keys) {
            if (clazz.isAssignableFrom(serviceClass)) return clazz
        }
        throw IllegalArgumentException("Unknown dependency for $serviceClass")
    }

    private fun buildCreators() {
        creators[JobCreator::class.java] = object : Creator<JobCreator>() {
            override fun create(): JobCreator {
                return VungleJobCreator(ctx, getOrBuild(PathProvider::class.java))
            }
        }
        creators[JobRunner::class.java] = object : Creator<JobRunner>() {
            override fun create(): JobRunner {
                return VungleJobRunner(
                    getOrBuild(JobCreator::class.java),
                    getOrBuild(Executors::class.java).jobExecutor,
                    JobRunnerThreadPriorityHelper()
                )
            }
        }
        creators[VungleApiClient::class.java] = object : Creator<VungleApiClient>() {
            override fun create(): VungleApiClient {
                return VungleApiClient(
                    ctx,
                    getOrBuild(Platform::class.java),
                    getOrBuild(FilePreferences::class.java)
                )
            }
        }
        creators[Platform::class.java] = object : Creator<Platform>() {
            override fun create(): Platform {
                val sdkExecutors = getOrBuild(
                    Executors::class.java
                )
                return AndroidPlatform(
                    ctx,
                    sdkExecutors.uaExecutor
                )
            }
        }
        creators[Executors::class.java] = object : Creator<Executors>() {
            override fun create(): Executors {
                return SDKExecutors()
            }
        }
        creators[OMInjector::class.java] = object : Creator<OMInjector>() {
            override fun create(): OMInjector {
                return OMInjector(ctx)
            }
        }
        creators[OMTracker.Factory::class.java] = object : Creator<OMTracker.Factory>() {
            override fun create(): OMTracker.Factory {
                return OMTracker.Factory()
            }
        }
        creators[FilePreferences::class.java] = object : Creator<FilePreferences>() {
            override fun create(): FilePreferences {
                return FilePreferences(
                    getOrBuild(Executors::class.java).ioExecutor,
                    getOrBuild(PathProvider::class.java)
                )
            }
        }
        creators[LocaleInfo::class.java] = object : Creator<LocaleInfo>() {
            override fun create(): LocaleInfo {
                return SystemLocaleInfo()
            }
        }
        creators[BidTokenEncoder::class.java] = object : Creator<BidTokenEncoder>() {
            override fun create(): BidTokenEncoder {
                return BidTokenEncoder(
                    ctx
                )
            }
        }
        creators[PathProvider::class.java] = object : Creator<PathProvider>() {
            override fun create(): PathProvider {
                return PathProvider(ctx)
            }
        }
        creators[Downloader::class.java] = object : Creator<Downloader>(isSingleton = false) {
            override fun create(): Downloader {
                return AssetDownloader(
                    getOrBuild(Executors::class.java).downloaderExecutor,
                    getOrBuild(PathProvider::class.java)
                )
            }
        }
        creators[ConcurrencyTimeoutProvider::class.java] =
            object : Creator<ConcurrencyTimeoutProvider>() {
                override fun create(): ConcurrencyTimeoutProvider {
                    return ConcurrencyTimeoutProvider()
                }
            }
    }

    private abstract inner class Creator<T>(val isSingleton: Boolean = true) {
        abstract fun create(): T
    }

    @Keep
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        @VisibleForTesting
        internal var INSTANCE: ServiceLocator? = null

        fun getInstance(context: Context): ServiceLocator =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ServiceLocator(context).also { INSTANCE = it }
            }

        inline fun <reified T : Any> inject(context: Context): Lazy<T> {
            return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
                getInstance(context).getOrBuild(T::class.java)
            }
        }

        @Synchronized
        fun deInit() {
            INSTANCE = null
        }
    }

}
