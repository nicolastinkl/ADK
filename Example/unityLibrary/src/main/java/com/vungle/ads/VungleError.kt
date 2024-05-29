package com.vungle.ads

import com.vungle.ads.internal.protos.Sdk.SDKError

/** Class that describes errors that can be thrown by Vungle SDK.
 * @param errorCode Integer code, unique to error type. Can be null if `loggableReason` is not
 * @param loggableReason [SDKError.Reason] that can/will be used to log this error. Can be null if
 * `errorCode` is not
 * @param errorMessage String message that describes in detail what happened
 * */
  sealed class VungleError(
    errorCode: Int? = null,
    val loggableReason: SDKError.Reason? = null,
    errorMessage: String? = null,
    var placementId: String? = null,
    var creativeId: String? = null,
    var eventId: String? = null,
) : Exception(errorMessage ?: getLocalizedMessage(errorCode ?: DEFAULT)) {

    val code = errorCode ?: loggableReason!!.number
    val errorMessage = errorMessage ?: getLocalizedMessage(errorCode ?: DEFAULT)

    init {
        if (BuildConfig.DEBUG && loggableReason == null && errorCode == null) { //no error code available
            throw IllegalArgumentException(
                "At least one of `errorCode` and `loggableReason` has to be non-null"
            )
        }
    }

    override fun getLocalizedMessage(): String? {
        return errorMessage
    }

    internal fun logError(): VungleError {
        logErrorNoReturnValue()
        return this
    }

    internal fun logErrorNoReturnValue() {
        if (loggableReason != null) {
            AnalyticsClient.logError(loggableReason, errorMessage, placementId, creativeId, eventId)
        }
    }

    internal fun setPlacementId(placementId: String?): VungleError {
        this.placementId = placementId
        return this
    }

    internal fun setCreativeId(creativeId: String?): VungleError {
        this.creativeId = creativeId
        return this
    }

    internal fun setEventId(eventId: String?): VungleError {
        this.eventId = eventId
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VungleError

        if (code != other.code) return false
        if (loggableReason != other.loggableReason) return false
        if (errorMessage != other.errorMessage) return false
        if (placementId != other.placementId) return false
        if (creativeId != other.creativeId) return false
        if (eventId != other.eventId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + loggableReason.hashCode()
        result = 31 * result + errorMessage.hashCode()
        result = 31 * result + (placementId?.hashCode() ?: 0)
        result = 31 * result + (creativeId?.hashCode() ?: 0)
        result = 31 * result + (eventId?.hashCode() ?: 0)
        return result
    }


    companion object {

        /*--- Codes ---*/
        /*--- PROTO ---*/
        const val UNRECOGNIZED: Int = -1

        const val UNKNOWN_ERROR: Int = SDKError.Reason.UNKNOWN_ERROR_VALUE
        const val INVALID_APP_ID: Int = SDKError.Reason.INVALID_APP_ID_VALUE
        const val CURRENTLY_INITIALIZING: Int = SDKError.Reason.CURRENTLY_INITIALIZING_VALUE
        const val ALREADY_INITIALIZED: Int = SDKError.Reason.ALREADY_INITIALIZED_VALUE
        const val SDK_NOT_INITIALIZED: Int = SDKError.Reason.SDK_NOT_INITIALIZED_VALUE
        const val USER_AGENT_ERROR: Int = SDKError.Reason.USER_AGENT_ERROR_VALUE
        const val API_REQUEST_ERROR: Int = SDKError.Reason.API_REQUEST_ERROR_VALUE
        const val API_RESPONSE_DATA_ERROR: Int = SDKError.Reason.API_RESPONSE_DATA_ERROR_VALUE
        const val API_RESPONSE_DECODE_ERROR: Int = SDKError.Reason.API_RESPONSE_DECODE_ERROR_VALUE
        const val API_FAILED_STATUS_CODE: Int = SDKError.Reason.API_FAILED_STATUS_CODE_VALUE
        const val INVALID_TEMPLATE_URL: Int = SDKError.Reason.INVALID_TEMPLATE_URL_VALUE
        const val INVALID_REQUEST_BUILDER_ERROR: Int = SDKError.Reason.INVALID_REQUEST_BUILDER_ERROR_VALUE
        const val TEMPLATE_UNZIP_ERROR: Int = SDKError.Reason.TEMPLATE_UNZIP_ERROR_VALUE
        const val INVALID_CTA_URL: Int = SDKError.Reason.INVALID_CTA_URL_VALUE
        const val INVALID_ASSET_URL: Int = SDKError.Reason.INVALID_ASSET_URL_VALUE
        const val ASSET_REQUEST_ERROR: Int = SDKError.Reason.ASSET_REQUEST_ERROR_VALUE
        const val ASSET_RESPONSE_DATA_ERROR: Int = SDKError.Reason.ASSET_RESPONSE_DATA_ERROR_VALUE
        const val ASSET_WRITE_ERROR: Int = SDKError.Reason.ASSET_WRITE_ERROR_VALUE
        const val INVALID_INDEX_URL: Int = SDKError.Reason.INVALID_INDEX_URL_VALUE
        const val GZIP_ENCODE_ERROR: Int = SDKError.Reason.GZIP_ENCODE_ERROR_VALUE
        const val ASSET_FAILED_STATUS_CODE: Int = SDKError.Reason.ASSET_FAILED_STATUS_CODE_VALUE
        const val PROTOBUF_SERIALIZATION_ERROR: Int = SDKError.Reason.PROTOBUF_SERIALIZATION_ERROR_VALUE
        const val JSON_ENCODE_ERROR: Int = SDKError.Reason.JSON_ENCODE_ERROR_VALUE
        const val TPAT_ERROR: Int = SDKError.Reason.TPAT_ERROR_VALUE
        const val INVALID_ADS_ENDPOINT: Int = SDKError.Reason.INVALID_ADS_ENDPOINT_VALUE
        const val INVALID_RI_ENDPOINT: Int = SDKError.Reason.INVALID_RI_ENDPOINT_VALUE
        const val INVALID_LOG_ERROR_ENDPOINT: Int = SDKError.Reason.INVALID_LOG_ERROR_ENDPOINT_VALUE
        const val INVALID_METRICS_ENDPOINT: Int = SDKError.Reason.INVALID_METRICS_ENDPOINT_VALUE
        const val ASSET_FAILED_INSUFFICIENT_SPACE: Int = SDKError.Reason.ASSET_FAILED_INSUFFICIENT_SPACE_VALUE
        const val ASSET_FAILED_MAX_SPACE_EXCEEDED: Int = SDKError.Reason.ASSET_FAILED_MAX_SPACE_EXCEEDED_VALUE
        const val INVALID_TPAT_KEY: Int = SDKError.Reason.INVALID_TPAT_KEY_VALUE
        const val EMPTY_TPAT_ERROR: Int = SDKError.Reason.EMPTY_TPAT_ERROR_VALUE
        const val MRAID_DOWNLOAD_JS_ERROR: Int = SDKError.Reason.MRAID_DOWNLOAD_JS_ERROR_VALUE
        const val MRAID_JS_WRITE_FAILED: Int = SDKError.Reason.MRAID_JS_WRITE_FAILED_VALUE
        const val OMSDK_DOWNLOAD_JS_ERROR: Int = SDKError.Reason.OMSDK_DOWNLOAD_JS_ERROR_VALUE
        const val OMSDK_JS_WRITE_FAILED: Int = SDKError.Reason.OMSDK_JS_WRITE_FAILED_VALUE
        const val STORE_REGION_CODE_ERROR: Int = SDKError.Reason.STORE_REGION_CODE_ERROR_VALUE
        const val INVALID_CONFIG_RESPONSE: Int = SDKError.Reason.INVALID_CONFIG_RESPONSE_VALUE
        const val PRIVACY_URL_ERROR: Int = SDKError.Reason.PRIVACY_URL_ERROR_VALUE
        const val INVALID_EVENT_ID_ERROR: Int = SDKError.Reason.INVALID_EVENT_ID_ERROR_VALUE
        const val INVALID_PLACEMENT_ID: Int = SDKError.Reason.INVALID_PLACEMENT_ID_VALUE
        const val AD_CONSUMED: Int = SDKError.Reason.AD_CONSUMED_VALUE
        const val AD_IS_LOADING: Int = SDKError.Reason.AD_IS_LOADING_VALUE
        const val AD_ALREADY_LOADED: Int = SDKError.Reason.AD_ALREADY_LOADED_VALUE
        const val AD_IS_PLAYING: Int = SDKError.Reason.AD_IS_PLAYING_VALUE
        const val AD_ALREADY_FAILED: Int = SDKError.Reason.AD_ALREADY_FAILED_VALUE
        const val PLACEMENT_AD_TYPE_MISMATCH = SDKError.Reason.PLACEMENT_AD_TYPE_MISMATCH_VALUE
        const val INVALID_BID_PAYLOAD: Int = SDKError.Reason.INVALID_BID_PAYLOAD_VALUE
        const val INVALID_JSON_BID_PAYLOAD: Int = SDKError.Reason.INVALID_JSON_BID_PAYLOAD_VALUE
        const val AD_NOT_LOADED: Int = SDKError.Reason.AD_NOT_LOADED_VALUE
        const val PLACEMENT_SLEEP: Int = SDKError.Reason.PLACEMENT_SLEEP_VALUE
        const val INVALID_ADUNIT_BID_PAYLOAD: Int = SDKError.Reason.INVALID_ADUNIT_BID_PAYLOAD_VALUE
        const val INVALID_GZIP_BID_PAYLOAD: Int = SDKError.Reason.INVALID_GZIP_BID_PAYLOAD_VALUE
        const val AD_RESPONSE_EMPTY: Int = SDKError.Reason.AD_RESPONSE_EMPTY_VALUE
        const val AD_RESPONSE_INVALID_TEMPLATE_TYPE: Int = SDKError.Reason.AD_RESPONSE_INVALID_TEMPLATE_TYPE_VALUE
        const val AD_RESPONSE_TIMED_OUT: Int = SDKError.Reason.AD_RESPONSE_TIMED_OUT_VALUE
        const val MRAID_JS_DOES_NOT_EXIST: Int = SDKError.Reason.MRAID_JS_DOES_NOT_EXIST_VALUE
        const val MRAID_JS_COPY_FAILED: Int = SDKError.Reason.MRAID_JS_COPY_FAILED_VALUE
        const val AD_RESPONSE_RETRY_AFTER: Int = SDKError.Reason.AD_RESPONSE_RETRY_AFTER_VALUE
        const val AD_LOAD_FAIL_RETRY_AFTER: Int = SDKError.Reason.AD_LOAD_FAIL_RETRY_AFTER_VALUE
        const val MRAID_ERROR: Int = SDKError.Reason.MRAID_ERROR_VALUE
        const val INVALID_IFA_STATUS: Int = SDKError.Reason.INVALID_IFA_STATUS_VALUE
        const val AD_EXPIRED: Int = SDKError.Reason.AD_EXPIRED_VALUE
        const val MRAID_BRIDGE_ERROR: Int = SDKError.Reason.MRAID_BRIDGE_ERROR_VALUE
        const val AD_EXPIRED_ON_PLAY: Int = SDKError.Reason.AD_EXPIRED_ON_PLAY_VALUE
        const val AD_WIN_NOTIFICATION_ERROR: Int = SDKError.Reason.AD_WIN_NOTIFICATION_ERROR_VALUE
        const val ASSET_FAILED_TO_DELETE: Int = SDKError.Reason.ASSET_FAILED_TO_DELETE_VALUE
        const val AD_HTML_FAILED_TO_LOAD: Int = SDKError.Reason.AD_HTML_FAILED_TO_LOAD_VALUE
        const val MRAID_JS_CALL_EMPTY: Int = SDKError.Reason.MRAID_JS_CALL_EMPTY_VALUE
        const val DEEPLINK_OPEN_FAILED: Int = SDKError.Reason.DEEPLINK_OPEN_FAILED_VALUE
        const val EVALUATE_JAVASCRIPT_FAILED: Int = SDKError.Reason.EVALUATE_JAVASCRIPT_FAILED_VALUE
        const val LINK_COMMAND_OPEN_FAILED: Int = SDKError.Reason.LINK_COMMAND_OPEN_FAILED_VALUE
        const val JSON_PARAMS_ENCODE_ERROR: Int = SDKError.Reason.JSON_PARAMS_ENCODE_ERROR_VALUE
        const val GENERATE_JSON_DATA_ERROR: Int = SDKError.Reason.GENERATE_JSON_DATA_ERROR_VALUE
        const val AD_CLOSED_TEMPLATE_ERROR: Int = SDKError.Reason.AD_CLOSED_TEMPLATE_ERROR_VALUE
        const val AD_CLOSED_MISSING_HEARTBEAT: Int = SDKError.Reason.AD_CLOSED_MISSING_HEARTBEAT_VALUE
        const val CONCURRENT_PLAYBACK_UNSUPPORTED: Int = SDKError.Reason.CONCURRENT_PLAYBACK_UNSUPPORTED_VALUE
        const val BANNER_VIEW_INVALID_SIZE: Int = SDKError.Reason.BANNER_VIEW_INVALID_SIZE_VALUE
        const val NATIVE_ASSET_ERROR: Int = SDKError.Reason.NATIVE_ASSET_ERROR_VALUE
        const val WEB_VIEW_WEB_CONTENT_PROCESS_DID_TERMINATE: Int = SDKError.Reason.WEB_VIEW_WEB_CONTENT_PROCESS_DID_TERMINATE_VALUE
        const val WEB_VIEW_FAILED_NAVIGATION: Int = SDKError.Reason.WEB_VIEW_FAILED_NAVIGATION_VALUE
        const val STORE_KIT_LOAD_ERROR: Int = SDKError.Reason.STORE_KIT_LOAD_ERROR_VALUE
        const val OMSDK_COPY_ERROR: Int = SDKError.Reason.OMSDK_COPY_ERROR_VALUE
        const val STORE_OVERLAY_LOAD_ERROR: Int = SDKError.Reason.STORE_OVERLAY_LOAD_ERROR_VALUE
        const val REACHABILITY_INITIALIZATION_FAILED: Int = SDKError.Reason.REACHABILITY_INITIALIZATION_FAILED_VALUE
        const val UNKNOWN_RADIO_ACCESS_TECHNOLOGY: Int = SDKError.Reason.UNKNOWN_RADIO_ACCESS_TECHNOLOGY_VALUE
        const val INVALID_WATERFALL_PLACEMENT: Int = SDKError.Reason.INVALID_WATERFALL_PLACEMENT_ID_VALUE
        const val OUT_OF_MEMORY: Int = SDKError.Reason.OUT_OF_MEMORY_VALUE
        const val TPAT_RETRY_FAILED = SDKError.Reason.TPAT_RETRY_FAILED_VALUE

        /*--- DROID ---*/
        const val DEFAULT = 10000
        const val NO_SERVE = 10001

        const val CONFIGURATION_ERROR = 10003

        const val AD_UNABLE_TO_PLAY = 10010
        const val AD_FAILED_TO_DOWNLOAD = 10011

        const val PLACEMENT_NOT_FOUND = 10013
        const val SERVER_RETRY_ERROR = 10014
        const val ALREADY_PLAYING_ANOTHER_AD = 10015

        const val NO_SPACE_TO_DOWNLOAD_ASSETS = 10019
        const val NETWORK_ERROR = 10020

        const val ASSET_DOWNLOAD_ERROR = 10024

        const val INVALID_SIZE = 10028

        const val WEB_CRASH = 10031
        const val WEBVIEW_RENDER_UNRESPONSIVE = 10032
        const val NETWORK_UNREACHABLE = 10033
        const val NETWORK_PERMISSIONS_NOT_GRANTED = 10034
        const val SDK_VERSION_BELOW_REQUIRED_VERSION = 10035

        const val AD_RENDER_NETWORK_ERROR = 10038

        const val AD_MARKUP_INVALID = 10040
        const val CREATIVE_ERROR = 10041
        const val INVALID_AD_STATE = 10042
        const val HEARTBEAT_ERROR = 10043

        const val NETWORK_TIMEOUT = 10047
        const val UNKNOWN_EXCEPTION_CODE = 10048
        /* when adding new exception codes above please don't forget to add them to map below */

        private val EXCEPTION_CODE_TO_MESSAGE_MAP : Map<Int, String> = HashMap<Int, String>().apply {
            put(DEFAULT, "")
            put(CONFIGURATION_ERROR, "Configuration Error Occurred. Please check your appID and " +
                    "placementIDs, and try again when network connectivity is available.")
            put(NO_SERVE, "No advertisements are available for your current bid. Please try again later.")
            put(UNKNOWN_ERROR, "Unknown Error Occurred.")
            put(AD_EXPIRED, "The advertisement in the cache has expired and can no longer be played." +
                    " Please load another ad")
            put(CURRENTLY_INITIALIZING, "There is already an ongoing operation for the action you requested." +
                    " Please wait until the operation finished before starting another.")
            put(SDK_NOT_INITIALIZED, "Vungle is not initialized/no longer initialized. " +
                    "Please call Vungle.init() to reinitialize.")
            put(AD_UNABLE_TO_PLAY, "Unable to play advertisement")
            put(AD_FAILED_TO_DOWNLOAD, "Advertisement failed to download")
            put(PLACEMENT_NOT_FOUND, "Placement is not valid")
            put(SERVER_RETRY_ERROR, "Remote Server responded with http Retry-After, SDK will retry " +
                    "this request.")
            put(ALREADY_PLAYING_ANOTHER_AD, "Vungle is already playing different ad.")
            put(NO_SPACE_TO_DOWNLOAD_ASSETS, "There is not enough file system size on a device to " +
                    "download assets for an ad.")
            put(NETWORK_ERROR, NETWORK_ERROR_MESSAGE)
            put(ASSET_DOWNLOAD_ERROR, "Assets download failed.")
            put(INVALID_SIZE, "Ad size is invalid")
            put(WEB_CRASH, "Android web view has crashed")
            put(WEBVIEW_RENDER_UNRESPONSIVE, "Android web view render became unresponsive, please " +
                    "clean-up your Webview process if any")
            put(NETWORK_UNREACHABLE, "Network error. Please check if network is available and " +
                    "permission for network access is granted.")
            put(NETWORK_PERMISSIONS_NOT_GRANTED, "Network permissions not granted. Please check " +
                    "manifest for android.permission.INTERNET and android.permission.ACCESS_NETWORK_STATE")
            put(SDK_VERSION_BELOW_REQUIRED_VERSION, "The SDK minimum version should not be overridden. " +
                    "Will not work as expected.")
            put(AD_RENDER_NETWORK_ERROR, "Ad rendering failed due to network connectivity issue")
            put(OUT_OF_MEMORY, "Out of memory")
            put(AD_MARKUP_INVALID, "Invalid ad markup")
            put(CREATIVE_ERROR, "Creative error occurred")
            put(INVALID_AD_STATE, "Invalid ad state ")
            put(HEARTBEAT_ERROR, "Heartbeat not received within a valid time window")
            put(INVALID_APP_ID, "App id is invalid.")
            put(PLACEMENT_AD_TYPE_MISMATCH, PLACEMENT_AD_TYPE_MISMATCH_MESSAGE)
            put(CONCURRENT_PLAYBACK_UNSUPPORTED, CONCURRENT_PLAYBACK_UNSUPPORTED_MESSAGE)
            put(NETWORK_TIMEOUT, "Request timeout.")
            put(TPAT_RETRY_FAILED, TPAT_RETRY_FAILED_MESSAGE)
            put(MRAID_DOWNLOAD_JS_ERROR, "Failed to download mraid js.")
            put(ASSET_RESPONSE_DATA_ERROR, "Server returned an unexpected response object or " +
                    "failed to load the downloaded data.")
            put(INVALID_WATERFALL_PLACEMENT, "Placement header bidding type does not match with loadAd call.")
            put(PRIVACY_URL_ERROR, PRIVACY_URL_ERROR_MESSAGE)
        }

        fun getLocalizedMessage(exceptionCode: Int): String {
            val message = EXCEPTION_CODE_TO_MESSAGE_MAP[exceptionCode]
            if (message == null) {
                UnknownExceptionCode("No adequate description for exceptionCode=$exceptionCode")
                    .logErrorNoReturnValue()
            }
            return message ?: "Unknown Exception Code"
        }

        fun codeToLoggableReason(errorCode: Int): SDKError.Reason = SDKError.Reason.forNumber(errorCode)
    }
}
/*--- Messages ---*/
private const val NETWORK_ERROR_MESSAGE = "Network error. Try again later"
private const val CONCURRENT_PLAYBACK_UNSUPPORTED_MESSAGE = "Concurrent playback not supported"
private const val TPAT_RETRY_FAILED_MESSAGE = "Pinging Tpat did not succeed during all allowed reries."
private const val INVALID_WATERFALL_PLACEMENT_MESSAGE = "header bidding status does not match with loadAd parameters"
private const val PRIVACY_URL_ERROR_MESSAGE = "Failed to open privacy url"
private const val PLACEMENT_AD_TYPE_MISMATCH_MESSAGE = "Ad type does not match with placement type."

class InternalError(errorCode : Int, errorMessage: String? = null)
    : VungleError(errorCode = errorCode, errorMessage = errorMessage) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return this.code == (other as InternalError).code
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

class UnknownExceptionCode(errorMessage: String) : VungleError(
    errorCode = UNKNOWN_EXCEPTION_CODE,
    loggableReason = SDKError.Reason.UNKNOWN_ERROR,
    errorMessage = errorMessage,
)

class InvalidAppId : VungleError(
    errorCode = INVALID_APP_ID,
    loggableReason = SDKError.Reason.INVALID_APP_ID,
    errorMessage = "App ID is empty",
)

class SdkVersionTooLow : VungleError(
    errorCode = SDK_VERSION_BELOW_REQUIRED_VERSION,
    loggableReason = SDKError.Reason.API_REQUEST_ERROR,
    errorMessage = "Config: SDK is supported only for API versions 21 and above",
)

class SdkAlreadyInitialized : VungleError(
    loggableReason = SDKError.Reason.ALREADY_INITIALIZED,
    errorMessage = "Config: Vungle SDK is already initialized",
)

class NetworkPermissionsNotGranted : VungleError(
    errorCode = NETWORK_PERMISSIONS_NOT_GRANTED,
)

class SdkInitializationInProgress : VungleError(
    errorCode = CURRENTLY_INITIALIZING,
    loggableReason = SDKError.Reason.CURRENTLY_INITIALIZING,
    errorMessage = "Config: Init Ongoing",
)

class OutOfMemory : VungleError(
    errorCode = OUT_OF_MEMORY,
    loggableReason = SDKError.Reason.OUT_OF_MEMORY,
    errorMessage = "Config: Out of Memory",
)

class SdkNotInitialized : VungleError(
    errorCode = CONFIGURATION_ERROR,
    loggableReason = SDKError.Reason.SDK_NOT_INITIALIZED,
    errorMessage = "Config: SDK response is null",
)

class AdRetryActiveError : VungleError(
    errorCode = SERVER_RETRY_ERROR,
    loggableReason = SDKError.Reason.AD_LOAD_FAIL_RETRY_AFTER,
    errorMessage = "Ads: Server Retry Error active",
)

class AdRetryError : VungleError(
    errorCode = SERVER_RETRY_ERROR,
    loggableReason = SDKError.Reason.AD_RESPONSE_RETRY_AFTER,
    errorMessage = "Ads retry-after: Server is busy",
)

class ConfigurationError : VungleError(
    errorCode = CONFIGURATION_ERROR,
    loggableReason = SDKError.Reason.API_REQUEST_ERROR,
    errorMessage = "Config: Configuration Error",
)

class ConfigurationResponseError : VungleError(
    errorCode = CONFIGURATION_ERROR,
    loggableReason = SDKError.Reason.INVALID_CONFIG_RESPONSE,
    errorMessage = "Config: Configuration failed due to bad response.",
)

class NetworkUnreachable : VungleError(
    errorCode = NETWORK_UNREACHABLE,
    loggableReason = SDKError.Reason.API_REQUEST_ERROR,
    errorMessage = "Config: Network Unreachable",
)

class UnknownConfigurationError : VungleError(
    errorCode = UNKNOWN_ERROR,
    loggableReason = SDKError.Reason.UNKNOWN_ERROR,
    errorMessage = "Config: Unknown Error",
)


class UnknownsConfigurationError : VungleError(
    errorCode = UNKNOWN_ERROR,
    loggableReason = SDKError.Reason.UNKNOWN_ERROR,
    errorMessage = "Config: Unknown Nothing happend",
)

class PlacementNotFoundError(placementId: String) : VungleError(
    INVALID_PLACEMENT_ID,
    loggableReason = SDKError.Reason.INVALID_PLACEMENT_ID,
    "Placement \'$placementId\' is invalid",
    placementId
)

/*
class Error(
loggableReason: SDKError.Reason = SDKError.Reason.INVALID_JSON_BID_PAYLOAD,
exception: Throwable? = null,
placementId: String?,
eventId: String?,
) :
VungleError(
    errorCode = AD_MARKUP_INVALID,
    loggableReason = loggableReason,
    errorMessage = "Unable to decode payload into BidPayload object. Error: ${exception?.toString()}",
    placementId = placementId,
    eventId = eventId
)
*/

class AdExpiredError : VungleError(
    AD_EXPIRED,
    loggableReason = SDKError.Reason.AD_EXPIRED,
    "Ad expired",
)

class AdExpiredOnPlayError : VungleError(
    AD_EXPIRED_ON_PLAY,
    loggableReason = SDKError.Reason.AD_EXPIRED_ON_PLAY,
    "Ad expired upon playback request",
)

class AdNotLoadedCantPlay : VungleError(
    AD_UNABLE_TO_PLAY,
    loggableReason = SDKError.Reason.AD_NOT_LOADED,
    "Failed to retrieve the ad object.",
)

class AdCantPlayWithoutWebView : VungleError(
    AD_UNABLE_TO_PLAY,
    loggableReason = SDKError.Reason.AD_HTML_FAILED_TO_LOAD,
    "No WebView when playing ads.",
)

class InvalidAdStateError(
    errorCode: Int = INVALID_AD_STATE,
    loggableReason: SDKError.Reason? = null,
    errorMessage: String = "Ad state is invalid",
    placementId: String? = null,
    creativeId: String? = null,
    eventId: String? = null,
) : VungleError(
    errorCode,
    loggableReason,
    errorMessage,
    placementId,
    creativeId,
    eventId
)

class NoServeError : VungleError(NO_SERVE)

class ConcurrentPlaybackUnsupported : VungleError(
    CONCURRENT_PLAYBACK_UNSUPPORTED,
    SDKError.Reason.CONCURRENT_PLAYBACK_UNSUPPORTED,
    CONCURRENT_PLAYBACK_UNSUPPORTED_MESSAGE,
)

class TpatRetryFailure(url: String) : VungleError(
    TPAT_RETRY_FAILED,
    SDKError.Reason.TPAT_RETRY_FAILED,
    "$TPAT_RETRY_FAILED_MESSAGE Failed url is $url"
)

class MraidJsError(errorMessage: String? = null) : VungleError(
    MRAID_DOWNLOAD_JS_ERROR,
    SDKError.Reason.MRAID_DOWNLOAD_JS_ERROR,
    errorMessage = errorMessage
)

class AdFailedToDownloadError : VungleError(AD_FAILED_TO_DOWNLOAD)

class AdMarkupInvalidError : VungleError(AD_MARKUP_INVALID)

class AssetDownloadError : VungleError(ASSET_DOWNLOAD_ERROR)

class AssetFailedStatusCodeError(url: String, code: Int? = null, placementId: String? = null) : VungleError(
    ASSET_FAILED_STATUS_CODE,
    SDKError.Reason.ASSET_FAILED_STATUS_CODE,
    errorMessage = "Asset fail to download: $url, Error code:$code",
    placementId = placementId

)

class InvalidWaterfallPlacementError(placementId: String?): VungleError(
    INVALID_WATERFALL_PLACEMENT,
    SDKError.Reason.INVALID_WATERFALL_PLACEMENT_ID,
    errorMessage = "$placementId $INVALID_WATERFALL_PLACEMENT_MESSAGE",
    placementId = placementId
)

class PrivacyUrlError(privacyUrl: String): VungleError(
    PRIVACY_URL_ERROR,
    SDKError.Reason.PRIVACY_URL_ERROR,
    "$PRIVACY_URL_ERROR_MESSAGE: $privacyUrl"
)

class PlacementAdTypeMismatchError(placementId: String?): VungleError(
    PLACEMENT_AD_TYPE_MISMATCH,
    SDKError.Reason.PLACEMENT_AD_TYPE_MISMATCH,
    errorMessage = "$placementId $PLACEMENT_AD_TYPE_MISMATCH_MESSAGE",
    placementId = placementId
)
