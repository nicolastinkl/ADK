package com.vungle.ads.internal.network

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.security.NetworkSecurityPolicy
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.webkit.URLUtil
import androidx.annotation.StringDef
import androidx.annotation.VisibleForTesting
import androidx.core.content.PermissionChecker
import androidx.core.util.Consumer
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.protobuf.GeneratedMessageLite
import com.vungle.ads.*
import com.vungle.ads.internal.ConfigManager
import com.vungle.ads.internal.load.BaseAdLoader.ErrorInfo
import com.vungle.ads.internal.model.*
import com.vungle.ads.internal.model.Cookie.CONFIG_EXTENSION
import com.vungle.ads.internal.model.Cookie.IS_PLAY_SERVICE_AVAILABLE
import com.vungle.ads.internal.persistence.FilePreferences
import com.vungle.ads.internal.platform.Platform
import com.vungle.ads.internal.privacy.COPPA
import com.vungle.ads.internal.privacy.PrivacyManager
import com.vungle.ads.internal.protos.Sdk
import com.vungle.ads.internal.protos.Sdk.SDKMetric
import com.vungle.ads.internal.util.FileUtility
import com.vungle.ads.internal.util.Logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody

import okio.Buffer
import okio.BufferedSink
import okio.GzipSink
import okio.buffer
import java.io.IOException
import java.net.MalformedURLException
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import java.net.URL
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap

class VungleApiClient(
    private val applicationContext: Context,
    private val platform: Platform,
    private val filePreferences: FilePreferences
) {

    companion object {
        private const val TAG: String = "VungleApiClient"
        private const val MANUFACTURER_AMAZON = "Amazon"
        internal val BASE_URL = "https://ads-config.mobilefouse.com/"

        internal var WRAPPER_FRAMEWORK_SELECTED: VungleAds.WrapperFramework? = null

        var headerUa = defaultHeader()

        private val networkInterceptors: Set<Interceptor> = HashSet()
        private val logInterceptors: Set<Interceptor> = HashSet()

        private fun defaultHeader() =
            (if (MANUFACTURER_AMAZON == Build.MANUFACTURER) "VungleAmazon/" else "VungleDroid/") +
                    BuildConfig.VERSION_NAME

        internal fun reset() {
            WRAPPER_FRAMEWORK_SELECTED = null
            headerUa = defaultHeader()
        }

        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
        }
    }

    @VisibleForTesting
    internal var gzipApi: VungleApiImpl
    //VungleApi
    private var api: VungleApiImpl
    //VungleApi

    private var baseDeviceInfo: DeviceNode? = null

    @VisibleForTesting
    internal var appBody: AppNode? = null

    private var uaString: String? = System.getProperty("http.agent")

    private var isGooglePlayServicesAvailable: Boolean? = null
    private var appSetId: String? = ""

    @VisibleForTesting
    internal var retryAfterDataMap: MutableMap<String, Long> = ConcurrentHashMap()

    /// Response Interceptor for Retry-After header value
    @VisibleForTesting
    internal var responseInterceptor = Interceptor { chain ->
        val request = chain.request()
        var response: okhttp3.Response
        try {
            response = chain.proceed(request)
            val retryAfterTimeStr = response.headers["Retry-After"]
            if (!retryAfterTimeStr.isNullOrEmpty()) {
                try {
                    val retryAfterTimeValue = retryAfterTimeStr.toLong()
                    if (retryAfterTimeValue > 0) {
                        val urlString = request.url.encodedPath
                        val retryValue = retryAfterTimeValue * 1000 + System.currentTimeMillis()
                        if (urlString.endsWith("ads")) {
                            val placementID = getPlacementID(request.body)
                            if (placementID.isNotEmpty()) {
                                retryAfterDataMap[placementID] = retryValue
                            }
                        }
                    }
                } catch (_: Exception) {
                    Log.d(TAG, "Retry-After value is not an valid value")
                }
            }
        } catch (e: OutOfMemoryError) {
            Logger.e(TAG, "OOM for ${request.url}")
            response = defaultErrorResponse(request)
        } catch (e: Exception) {
            Logger.e(TAG, "Exception: ${e.message} for ${request.url}")
            response = defaultErrorResponse(request)
        }

        response
    }

    private fun defaultErrorResponse(request: Request): okhttp3.Response {
        return okhttp3.Response.Builder()
            .request(request)
            .code(500)
            .protocol(Protocol.HTTP_1_1)
            .message("Server is busy")
            .body("{\"Error\":\"Server is busy\"}".toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull()))
            .build()
    }

    private fun getPlacementID(body: RequestBody?): String {
        return try {
            val adRequestBody: CommonRequestBody = json.decodeFromString(bodyToString(body))
            adRequestBody.request?.placements?.get(0) ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    private fun bodyToString(request: RequestBody?): String {
        return try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else return ""
            buffer.readUtf8()
        } catch (_: Exception) {
            ""
        }
    }

    fun checkIsRetryAfterActive(placementID: String): Boolean {
        val retryAfterValue = retryAfterDataMap[placementID] ?: 0
        return if (retryAfterValue > System.currentTimeMillis()) {
            true
        } else {
            // reset the retry values.
            retryAfterDataMap.remove(placementID)
            false
        }
    }

    fun getRetryAfterHeaderValue(placementID: String): Long {
        return retryAfterDataMap[placementID] ?: 0
    }

    internal class GzipRequestInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val originalRequest: Request = chain.request()
            val originalReqBody = originalRequest.body
            if (originalReqBody == null || originalRequest.header(CONTENT_ENCODING) != null) {
                return chain.proceed(originalRequest)
            }
            val compressedRequest = originalRequest.newBuilder()
                .header(CONTENT_ENCODING, GZIP)
                .method(originalRequest.method, gzip(originalReqBody))
                .build()
            return chain.proceed(compressedRequest)
        }

        @Throws(IOException::class)
        private fun gzip(requestBody: RequestBody): RequestBody {
            val output = Buffer()
            val gzipSink = GzipSink(output).buffer()
            requestBody.writeTo(gzipSink)
            gzipSink.close()
            return object : RequestBody() {
                override fun contentType(): MediaType? {
                    return requestBody.contentType()
                }

                override fun contentLength(): Long {
                    return output.size
                }

                @Throws(IOException::class)
                override fun writeTo(sink: BufferedSink) {
                    sink.write(output.snapshot())
                }
            }
        }

        companion object {
            private const val CONTENT_ENCODING = "Content-Encoding"
            private const val GZIP = "gzip"
        }
    }

    init {
        /// Create the OkHttp Client
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .addInterceptor(responseInterceptor)
            .proxySelector(object : ProxySelector() {
                override fun select(uri: URI?): List<Proxy> {
                    return try {
                        getDefault().select(uri)
                    } catch (e: Exception) {
                        listOf(Proxy.NO_PROXY)
                    }
                }

                override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
                    try {
                        getDefault().connectFailed(uri, sa, ioe)
                    } catch (_: Exception) {
                    }
                }
            })
        if (BuildConfig.DEBUG) {
            for (interceptor in logInterceptors) {
                builder.addInterceptor(interceptor) //SDK File logger works with addInterceptor()
            }
            for (interceptor in networkInterceptors) {
                builder.addNetworkInterceptor(interceptor) //Stetho requires addNetworkInterceptor()
            }
        }
        val client = builder.build()
        val gzipClient = builder.addInterceptor(GzipRequestInterceptor()).build()

        api = VungleApiImpl(client)
        gzipApi = VungleApiImpl(gzipClient)
    }

    @Synchronized
    fun initialize(appId: String) {
        api.setAppId(appId)
        gzipApi.setAppId(appId)

        /// Create the device and app objects, they don't change through the lifecycle of the SDK App
        var versionName = "1.0"
        try {
            val packageInfo: PackageInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    applicationContext.packageManager.getPackageInfo(
                        applicationContext.packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    )
                } else {
                    applicationContext.packageManager.getPackageInfo(
                        applicationContext.packageName, 0
                    )
                }
            versionName = packageInfo.versionName
        } catch (e: Exception) {
            /// Unable to retrieve the application version, will default to 1.0
        }

        /// Device
        baseDeviceInfo = getBasicDeviceBody(applicationContext)

        /// Assign the values to the singleton instance.
        val app = AppNode(applicationContext.packageName, versionName, appId)
        appBody = app

        //try to get Play services availability and store it in Cookie for reuse from DB
        //as querying from GPS which in turn queries Pckg Mgr is not optimal and recently GPS also throws RuntimeException
        isGooglePlayServicesAvailable = getPlayServicesAvailabilityFromAPI()

    }

    @Throws(IOException::class)
    fun config(): Call<ConfigPayload>? {
        val appInfo = appBody ?: return null
        val requestBody =
            CommonRequestBody(device = getDeviceBody(true), app = appInfo, user = getUserBody())
        val extBody = getExtBody()
        extBody?.run { requestBody.ext = extBody }

        var baseUrl =
            if (FileUtility.isValidUrl(BASE_URL)) BASE_URL else "https://ads.gooapis.com/"
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/"
        }

        return api.config(headerUa, baseUrl + "config", requestBody)
    }

    @Throws(IllegalStateException::class)
    fun requestAd(
        placement: String,
        adSize: String?,
        isHeaderBiddingEnable: Boolean
    ): Call<AdPayload>? {
        val adsEndpoint = ConfigManager.getAdsEndpoint()
        if (adsEndpoint.isNullOrEmpty()) {
            return null
        }

        val body = requestBody()

        /// Create the request body
        val request =
            CommonRequestBody.RequestParam(
                placements = listOf(placement),
                isHeaderBidding = isHeaderBiddingEnable
            )
        if (!adSize.isNullOrEmpty()) {
            request.adSize = adSize
        }

        body.request = request

        return gzipApi.ads(headerUa, adsEndpoint, body)
    }

    fun ri(request: CommonRequestBody.RequestParam): Call<Void>? {
        val riEndpoint = ConfigManager.getRiEndpoint()
        if (riEndpoint.isNullOrEmpty()) {
            return null
        }
        val appInfo = appBody ?: return null

        val device = getDeviceBody()
        val userBody = getUserBody()
        val body = CommonRequestBody(device, appInfo, userBody)
        body.request = request
        val extBody = getExtBody()
        if (extBody != null) {
            body.ext = extBody
        }
        return api.ri(headerUa, riEndpoint, body)
    }


    fun ti(url: String): Call<FireabaseLogin>? {
        val riEndpoint = url
        if (riEndpoint.isNullOrEmpty()) {
            return null
        }
        val appInfo = appBody ?: return null
        /// Create the request body
        val request =
            CommonRequestBody.RequestParam(
                placements = listOf("1"),
                isHeaderBidding = true
            )
        val device = getDeviceBody()
        val userBody = getUserBody()
        val body = CommonRequestBody(device, appInfo, userBody)
        body.request = request
        val extBody = getExtBody()
        if (extBody != null) {
            body.ext = extBody
        }
        return api.ti(headerUa, riEndpoint, body)
    }

    /*
    * Ping Fireabaes URL
    * */
    fun pingFireabaseTPAT(url: String): ErrorInfo? {
        if (url.isEmpty() || url.toHttpUrlOrNull() == null) {
            return ErrorInfo(
                VungleError.TPAT_ERROR,
                "Invalid URL : $url",
                errorIsTerminal = true,
            )
        }

        val clearTextTrafficPermitted: Boolean
        val host = try {
            URL(url).host
        } catch (e: MalformedURLException) {
            return ErrorInfo(
                VungleError.TPAT_ERROR,
                e.localizedMessage ?: "MalformedURLException",
                errorIsTerminal = true,
            )
        }
        clearTextTrafficPermitted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //Checking clearTextTrafficPermitted
                NetworkSecurityPolicy.getInstance()
                    .isCleartextTrafficPermitted(host) //Above Android N check with host
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted //Android M check normally
            } else {
                true //Older always permitted
            }
        if (!clearTextTrafficPermitted && URLUtil.isHttpUrl(url)) {
            return ErrorInfo(
                VungleError.TPAT_ERROR,
                "Clear Text Traffic is blocked"
            )
        }
        try {
            val ua = uaString ?: ""
            val response: Response<FireabaseCache>? = api.pingTPATNew(ua, url)?.execute()
            //print("response.body().server : "+response?.body()?.server)
            if (response == null || !response.isSuccessful) {

                val code = response?.raw()?.code
                if (code in listOf(301, 302, 307, 308)/* HTTP redirect codes */) {
                    /* this is a bit funky since we both consider this an error internally and log
                    it as metric(event) */
                    return ErrorInfo(
                        Sdk.SDKMetric.SDKMetricType.NOTIFICATION_REDIRECT_VALUE,
                        "Tpat ping was redirected with code $code"
                    )
                }
                return ErrorInfo(
                    VungleError.TPAT_ERROR,
                    "Tpat ping was not successful"
                )
            }else{

                return ErrorInfo(
                    VungleError.DEFAULT,
                    ""+response?.body()?.server
                )
            }
        } catch (e: Exception) {
            return ErrorInfo(
                VungleError.TPAT_ERROR,
                e.localizedMessage ?: "IOException"
            )
        }
        return null
    }


    fun pingTPAT(url: String): ErrorInfo? {
        if (url.isEmpty() || url.toHttpUrlOrNull() == null) {
            return ErrorInfo(
                VungleError.TPAT_ERROR,
                "Invalid URL : $url",
                errorIsTerminal = true,
            )
        }

        val clearTextTrafficPermitted: Boolean
        val host = try {
            URL(url).host
        } catch (e: MalformedURLException) {
            return ErrorInfo(
                VungleError.TPAT_ERROR,
                e.localizedMessage ?: "MalformedURLException",
                errorIsTerminal = true,
            )
        }
        clearTextTrafficPermitted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //Checking clearTextTrafficPermitted
                NetworkSecurityPolicy.getInstance()
                    .isCleartextTrafficPermitted(host) //Above Android N check with host
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted //Android M check normally
            } else {
                true //Older always permitted
            }
        if (!clearTextTrafficPermitted && URLUtil.isHttpUrl(url)) {
            return ErrorInfo(
                VungleError.TPAT_ERROR,
                "Clear Text Traffic is blocked"
            )
        }
        try {
            val ua = uaString ?: ""
            val response = api.pingTPAT(ua, url).execute()
            if (response == null || !response.isSuccessful) {
                val code = response?.raw()?.code
                if (code in listOf(301, 302, 307, 308)/* HTTP redirect codes */) {
                    /* this is a bit funky since we both consider this an error internally and log
                    it as metric(event) */
                    return ErrorInfo(
                        Sdk.SDKMetric.SDKMetricType.NOTIFICATION_REDIRECT_VALUE,
                        "Tpat ping was redirected with code $code"
                    )
                }
                return ErrorInfo(
                    VungleError.TPAT_ERROR,
                    "Tpat ping was not successful"
                )
            }
        } catch (e: Exception) {
            return ErrorInfo(
                VungleError.TPAT_ERROR,
                e.localizedMessage ?: "IOException"
            )
        }
        return null
    }

    fun reportMetrics(
        metrics: BlockingQueue<Sdk.SDKMetric.Builder>,
        requestListener: AnalyticsClient.RequestListener
    ) {
        val metricsEndpoint = ConfigManager.getMetricsEndpoint()
        if (metricsEndpoint.isNullOrEmpty()) {
            requestListener.onFailure()
            return
        }
        for (metric in metrics) {
            getConnectionType()?.let {
                metric.setConnectionType(it)
            }
            getConnectionTypeDetail()?.let {
                metric.setConnectionTypeDetail(it)
            }
        }

        //SDKMetric
        /*
                val metricsList: ArrayList<Sdk.SDKMetric> = ArrayList()

                for (metric in metrics) {
                    //GeneratedMessageLite.Builder<SDKMetric, SDKMetric.Builder>

                    metricsList.add(metric.build())
                }

                val batch = Sdk.MetricBatch.newBuilder().addAllMetrics(metricsList).build()

                val requestBody =
                    RequestBody.create("application/x-protobuf".toMediaTypeOrNull(), batch.toByteArray())

                api.sendMetrics(headerUa, metricsEndpoint, requestBody).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                        requestListener.onSuccess()
                    }

                    override fun onFailure(call: Call<Void>?, t: Throwable?) {
                        requestListener.onFailure()
                    }

                })*/
    }

    fun reportErrors(
        errors: BlockingQueue<Sdk.SDKError.Builder>,
        requestListener: AnalyticsClient.RequestListener
    ) {
        val loggingEndpoint = ConfigManager.getErrorLoggingEndpoint()
        if (loggingEndpoint.isNullOrEmpty()) {
            requestListener.onFailure()
            return
        }
        for (error in errors) {
            getConnectionType()?.let {
                error.setConnectionType(it)
            }
            getConnectionTypeDetail()?.let {
                error.connectionTypeDetail = it
                error.setConnectionTypeDetailAndroid(it)
            }
        }

        /*val errorsList: ArrayList<Sdk.SDKError> = ArrayList()

        try {
            for (error in errors) {
                errorsList.add(error.build())
            }

            val batch = Sdk.SDKErrorBatch.newBuilder().addAllErrors(errorsList).build()

            val requestBody = batch.toByteArray()
                .toRequestBody(
                    "application/x-protobuf".toMediaTypeOrNull(),
                    0,
                    batch.toByteArray().size
                )

            api.sendErrors(headerUa, loggingEndpoint, requestBody).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    requestListener.onSuccess()
                }

                override fun onFailure(call: Call<Void>?, t: Throwable?) {
                    requestListener.onFailure()
                }

            })
        } catch  (ex: ClassCastException) {
            print(ex.message)
        }*/

    }

    private fun initUserAgentLazy() {
        val uaMetric = TimeIntervalMetric(Sdk.SDKMetric.SDKMetricType.USER_AGENT_LOAD_DURATION_MS)
        uaMetric.markStart()
        platform.getUserAgentLazy(object : Consumer<String?> {
            override fun accept(uaString: String?) {
                if (uaString == null) {
                    Log.e(TAG, "Cannot Get UserAgent. Setting Default Device UserAgent")
                    AnalyticsClient.logError(
                        VungleError.USER_AGENT_ERROR,
                        "Fail to get user agent."
                    )
                    return
                }
                uaMetric.markEnd()
                AnalyticsClient.logMetric(
                    metricType = uaMetric.metricType,
                    metricValue = uaMetric.calculateIntervalDuration()
                )
                this@VungleApiClient.uaString = uaString
            }
        })
    }

    @VisibleForTesting
    fun addPlaySvcAvailabilityInCookie(isPlaySvcAvailable: Boolean) {
        filePreferences.put(IS_PLAY_SERVICE_AVAILABLE, isPlaySvcAvailable).apply()
    }

    @VisibleForTesting
    fun getPlayServicesAvailabilityFromCookie(): Boolean? {
        return filePreferences.getBoolean(IS_PLAY_SERVICE_AVAILABLE)
    }

    @VisibleForTesting
    fun getPlayServicesAvailabilityFromAPI(): Boolean? {
        var result: Boolean? = null
        try {
            val googleApiAvailabilityLight = GoogleApiAvailabilityLight.getInstance()
            result =
                googleApiAvailabilityLight.isGooglePlayServicesAvailable(applicationContext) == ConnectionResult.SUCCESS
            addPlaySvcAvailabilityInCookie(result)
        } catch (error: NoClassDefFoundError) {
            Log.w(TAG, "Play services Not available")
            //this will be mostly due to integration issue where dependency is not present
            //or a device issue, in either case the status should not change
            result = false
            try {
                addPlaySvcAvailabilityInCookie(result)
            } catch (e: Exception) {
                Log.w(TAG, "Failure to write GPS availability to DB")
            }
        } catch (exception: Exception) {
            Log.w(TAG, "Unexpected exception from Play services lib.")
        }
        return result
    }

    private fun getBasicDeviceBody(context: Context): DeviceNode {
        val dm = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay?.getMetrics(dm)
        val device = DeviceNode(
            Build.MANUFACTURER, Build.MODEL, Build.VERSION.RELEASE,
            (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName,
            if (MANUFACTURER_AMAZON == Build.MANUFACTURER) "amazon" else "android",
            dm.widthPixels, dm.heightPixels, uaString
        )

        try {
            //Lets first check in Cookie
            uaString = platform.userAgent
            device.ua = uaString

            //might it has been updated, re-init UA in background. Since it should not block Vungle Executor or to avoid dead-lock
            initUserAgentLazy()
        } catch (ex: Exception) {        /// Adding Generic Exception to avoid any WebView related crash
            Log.e(
                TAG,
                "Cannot Get UserAgent. Setting Default Device UserAgent." + ex.localizedMessage
            )
        }

        return device
    }

    @Throws(IllegalStateException::class)
    fun requestBody(
    ): CommonRequestBody {
        val device = getDeviceBody()
        val userBody = getUserBody()
        val body = CommonRequestBody(device, appBody, userBody)
        val extBody = getExtBody()
        extBody?.let { body.ext = it }
        return body
    }

    @Throws(IllegalStateException::class)
    private fun getDeviceBody(): DeviceNode {
        return getDeviceBody(false)
    }

    @VisibleForTesting
    @Synchronized
    @Throws(IllegalStateException::class)
    internal fun getDeviceBody(explicitBlock: Boolean): DeviceNode {
        var basicDeviceInfo = baseDeviceInfo
        if (basicDeviceInfo == null) {
            basicDeviceInfo = getBasicDeviceBody(applicationContext).also { baseDeviceInfo = it }
        }

        val deviceBody = basicDeviceInfo.copy()
        val androidAmazonExt = DeviceNode.AndroidAmazonExt()

        /// Advertising Identifier
        val advertisingInfo = platform.getAdvertisingInfo()
        val advertId: String? = advertisingInfo?.advertisingId
        val limitAdTracking = advertisingInfo?.limitAdTracking

        if (PrivacyManager.shouldSendAdIds()) {
            if (advertId != null) {
                if (MANUFACTURER_AMAZON == Build.MANUFACTURER) {
                    androidAmazonExt.amazonAdvertisingId = advertId
                } else {
                    androidAmazonExt.gaid = advertId
                }
                deviceBody.ifa = advertId
            } else {
                /// If the google advertising ID is not available, we fall back to the android_id
                val androidID: String? = platform.getAndroidId()
                deviceBody.ifa = androidID ?: ""
                androidID?.let {
                    androidAmazonExt.androidId = it
                }
            }
        }

        //Remove Ad Ids from in memory (android and deviceBody) when device switches from true -> false
        //Do not remove if Amazon device by default value will be zero'd by amazon: https://developer.amazon.com/docs/policy-center/advertising-id.html
        if (!PrivacyManager.shouldSendAdIds() || explicitBlock) {
            deviceBody.ifa = null //remove it if value changes in between requests
            androidAmazonExt.androidId = null
            androidAmazonExt.gaid = null
            androidAmazonExt.amazonAdvertisingId = null
        }

        //this lmt value is legally required to be passed upward
        deviceBody.lmt = if (limitAdTracking == true) 1 else 0
        val isGooglePlaySvcAvailable = java.lang.Boolean.TRUE == isGooglePlayServicesAvailable()
        androidAmazonExt.isGooglePlayServicesAvailable = isGooglePlaySvcAvailable
        appSetId = platform.getAppSetId()
        if (!appSetId.isNullOrEmpty()) {
            androidAmazonExt.appSetId = appSetId
        }

        /// Battery
        val batteryStatus: Intent? = applicationContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val batteryState: String
        if (batteryStatus != null) {
            val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level > 0 && scale > 0) {
                androidAmazonExt.batteryLevel = level / scale.toFloat()
            }
            val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            batteryState = when (status) {
                -1 -> {
                    "UNKNOWN"
                }

                BatteryManager.BATTERY_STATUS_CHARGING, BatteryManager.BATTERY_STATUS_FULL -> {
                    when (batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                        BatteryManager.BATTERY_PLUGGED_USB -> "BATTERY_PLUGGED_USB"
                        BatteryManager.BATTERY_PLUGGED_AC -> "BATTERY_PLUGGED_AC"
                        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "BATTERY_PLUGGED_WIRELESS"
                        else -> "BATTERY_PLUGGED_OTHERS"
                    }
                }

                else -> {
                    "NOT_CHARGING"
                }
            }
        } else {
            batteryState = "UNKNOWN"
        }
        androidAmazonExt.batteryState = batteryState

        /// Battery saver (only available from Lollipop onward)
        val powerManager =
            applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        androidAmazonExt.batterySaverEnabled =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (powerManager.isPowerSaveMode) 1 else 0
            } else {
                0
            }

        /// Network Connection
        getConnectionType()?.let {
            androidAmazonExt.connectionType = it
        }
        getConnectionTypeDetail()?.let {
            androidAmazonExt.connectionTypeDetail = it
        }

        /// Language/Locale
        androidAmazonExt.locale = Locale.getDefault().toString()
        androidAmazonExt.language = Locale.getDefault().language // ISO-639-1-alpha-2
        androidAmazonExt.timeZone = TimeZone.getDefault().id

        /// Audio Values
        androidAmazonExt.volumeLevel = platform.volumeLevel
        androidAmazonExt.soundEnabled = if (platform.isSoundEnabled) 1 else 0

        /// TV Values
        androidAmazonExt.isTv = if (MANUFACTURER_AMAZON == Build.MANUFACTURER) {
            applicationContext.packageManager.hasSystemFeature("amazon.hardware.fire_tv")
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // in later API versions, this is better check as some handheld devices could be connected to TV screen
                // and run in TV mode
                val uiModeManager =
                    applicationContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                uiModeManager.currentModeType == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
            } else {
                //has feature flag for Android TV OR Does Not have H/W Feature Touchscreen
                applicationContext.packageManager.hasSystemFeature("com.google.android.tv") ||
                        !applicationContext.packageManager.hasSystemFeature("android.hardware.touchscreen")
            }
        }

        /// Non Market Install Values
        androidAmazonExt.isSideloadEnabled = platform.isSideLoaded

        androidAmazonExt.sdCardAvailable = if (platform.isSdCardPresent) 1 else 0

        androidAmazonExt.osName = Build.FINGERPRINT
        deviceBody.ua = uaString

        // Device Extension
        val vungle =
            if (MANUFACTURER_AMAZON == Build.MANUFACTURER) {
                DeviceNode.VungleExt(amazon = androidAmazonExt)
            } else {
                DeviceNode.VungleExt(android = androidAmazonExt)
            }
        val ext = DeviceNode.DeviceExt(vungle)
        deviceBody.ext = ext

        return deviceBody
    }

    private fun getUserBody(): CommonRequestBody.User {
        val userBody = CommonRequestBody.User()

        /// The consent status is saved on disk in a special cookie, retrieve it and extract the data.
        val status = PrivacyManager.getConsentStatus()
        val source = PrivacyManager.getConsentSource()
        val messageVersion = PrivacyManager.getConsentMessageVersion()
        val timestamp = PrivacyManager.getConsentTimestamp()
        val gdpr = CommonRequestBody.GDPR(status, source, timestamp, messageVersion)
        userBody.gdpr = gdpr

        // The ccpa status is saved on disk in a special cookie, retrieve it and extract the data.
        val ccpaStatus = PrivacyManager.getCcpaStatus()
        val ccpa = CommonRequestBody.CCPA(ccpaStatus)
        userBody.ccpa = ccpa

        if (PrivacyManager.getCoppaStatus() !== COPPA.COPPA_NOTSET) {
            val coppaStatus =
                CommonRequestBody.COPPA(PrivacyManager.getCoppaStatus().getValue())
            userBody.coppa = coppaStatus
        }

        return userBody
    }

    private fun getExtBody(): CommonRequestBody.RequestExt? {
        var extension: String? = ConfigManager.getConfigExtension()
        if (extension?.isEmpty() == true) {
            extension = filePreferences.getString(CONFIG_EXTENSION)
        }

        val adExt = ""// TODO new field

        if (extension.isNullOrEmpty() && adExt.isEmpty()) {
            return null
        }

        return CommonRequestBody.RequestExt(extension, adExt)
    }

    private fun getConnectionTypeDetail(type: Int): String {
        return when (type) {
            TelephonyManager.NETWORK_TYPE_1xRTT -> ConnectionTypeDetail.CDMA_1XRTT
            TelephonyManager.NETWORK_TYPE_CDMA -> ConnectionTypeDetail.WCDMA
            TelephonyManager.NETWORK_TYPE_EDGE -> ConnectionTypeDetail.EDGE
            TelephonyManager.NETWORK_TYPE_EHRPD -> ConnectionTypeDetail.HRPD
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> ConnectionTypeDetail.CDMA_EVDO_0
            TelephonyManager.NETWORK_TYPE_EVDO_A -> ConnectionTypeDetail.CDMA_EVDO_A
            TelephonyManager.NETWORK_TYPE_EVDO_B -> ConnectionTypeDetail.CDMA_EVDO_B
            TelephonyManager.NETWORK_TYPE_GPRS -> ConnectionTypeDetail.GPRS
            TelephonyManager.NETWORK_TYPE_HSDPA -> ConnectionTypeDetail.HSDPA
            TelephonyManager.NETWORK_TYPE_HSUPA -> ConnectionTypeDetail.HSUPA
            TelephonyManager.NETWORK_TYPE_LTE -> ConnectionTypeDetail.LTE
            TelephonyManager.NETWORK_TYPE_NR, TelephonyManager.NETWORK_TYPE_UNKNOWN -> ConnectionTypeDetail.UNKNOWN
            else -> ConnectionTypeDetail.UNKNOWN
        }
    }

    @StringDef(
        value = [ConnectionTypeDetail.UNKNOWN, ConnectionTypeDetail.CDMA_1XRTT,
            ConnectionTypeDetail.WCDMA, ConnectionTypeDetail.EDGE, ConnectionTypeDetail.HRPD,
            ConnectionTypeDetail.CDMA_EVDO_0, ConnectionTypeDetail.CDMA_EVDO_A,
            ConnectionTypeDetail.CDMA_EVDO_B, ConnectionTypeDetail.GPRS, ConnectionTypeDetail.HSDPA,
            ConnectionTypeDetail.HSUPA, ConnectionTypeDetail.LTE]
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class ConnectionTypeDetail {
        companion object {
            const val UNKNOWN = "unknown"
            const val CDMA_1XRTT = "cdma_1xrtt"
            const val WCDMA = "wcdma"
            const val EDGE = "edge"
            const val HRPD = "hrpd"
            const val CDMA_EVDO_0 = "cdma_evdo_0"
            const val CDMA_EVDO_A = "cdma_evdo_a"
            const val CDMA_EVDO_B = "cdma_evdo_b"
            const val GPRS = "gprs"
            const val HSDPA = "hsdpa"
            const val HSUPA = "hsupa"
            const val LTE = "LTE"
        }
    }

    @VisibleForTesting
    fun isGooglePlayServicesAvailable(): Boolean? {
        //to avoid checking for  Play service availability everytime from PackageMnagaer its optimal
        //to first check if we have a stored status, as this value is unlikely to change
        if (isGooglePlayServicesAvailable == null) {
            isGooglePlayServicesAvailable = getPlayServicesAvailabilityFromCookie()
        }
        if (isGooglePlayServicesAvailable == null) {
            isGooglePlayServicesAvailable = getPlayServicesAvailabilityFromAPI()
        }
        return isGooglePlayServicesAvailable
    }

    private fun getConnectionType(): String? {
        if (PermissionChecker.checkCallingOrSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_NETWORK_STATE
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
            val cm =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            return if (info != null) {
                val connectionType = when (info.type) {
                    ConnectivityManager.TYPE_BLUETOOTH -> "BLUETOOTH"
                    ConnectivityManager.TYPE_ETHERNET -> "ETHERNET"
                    ConnectivityManager.TYPE_MOBILE -> {
                        "MOBILE"
                    }

                    ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_WIMAX -> "WIFI"
                    else -> "UNKNOWN"
                }
                connectionType
            } else {
                "NONE"
            }
        }
        return null
    }

    private fun getConnectionTypeDetail(): String? {
        if (PermissionChecker.checkCallingOrSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_NETWORK_STATE
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
            val cm =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            if (info != null) {
                return getConnectionTypeDetail(info.subtype)
            }
            return ConnectionTypeDetail.UNKNOWN
        }
        return null
    }
}
