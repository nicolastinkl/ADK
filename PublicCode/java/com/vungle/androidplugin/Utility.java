package com.vungle.androidplugin;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.unity3d.player.UnityPlayer;


import org.json.JSONException;
import org.json.JSONObject;
//import org.json.simple.JSONObject;


/* loaded from: vunglePlugin.aar:classes.jar:com/vungle/androidplugin/Utility.class */
public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();
    private static boolean isLogEnabled = false;
    protected static final String TARGET_GAME_OBJECT = "VungleManager";

    /* loaded from: vunglePlugin.aar:classes.jar:com/vungle/androidplugin/Utility$LogLevel.class */
    public enum LogLevel {
        Error,
        Warning,
        Info,
        Debug,
        Verbose
    }

    public static void sendMessageToUnityGameObject(String methodName, String messageToSend) {
        if (messageToSend == null) {
            messageToSend = "";
        }
        try {
            UnityPlayer.UnitySendMessage(TARGET_GAME_OBJECT, methodName, messageToSend);
        } catch (Exception e) {
            LogMessage(LogLevel.Error, String.format("UnitySendMessage: %s.%s(%s) => %s", TARGET_GAME_OBJECT, methodName, messageToSend, e.getMessage()));
            e.printStackTrace();
        }
    }

    public static void setLogEnabled(boolean isEnabled) {
        isLogEnabled = isEnabled;
    }

    public static void notifyLogToUnity(String message) {
        if (isLogEnabled) {
            sendMessageToUnityGameObject("OnSDKLog", message);
            LogMessage(LogLevel.Debug, message);
        }
    }

    public static void notifyErrorToUnity(String message) {
        sendMessageToUnityGameObject("OnError", message);
    }

    public static void notifyErrorToUnity(String placementId, String error) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("placementID", placementId);
        message.put("error", error);

        sendMessageToUnityGameObject("OnError", message.toString());
    }

    public static void notifyWarningToUnity(String message) {
        sendMessageToUnityGameObject("OnWarning", message);
    }

    public static void notifyWarningToUnity(String placementId, String warning) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("placementID", placementId);
        message.put("warning", warning);
        sendMessageToUnityGameObject("OnWarning", message.toString());
    }

    public static void notifyInitToUnity(boolean success) {
        sendMessageToUnityGameObject("OnInitialize", success ? "1" : "0");
    }

    public static void notifyAdPlayableToUnity(String placementId, boolean available)  throws JSONException{
        JSONObject message = new JSONObject();
        message.put("placementID", placementId);
        message.put("isAdAvailable", new Boolean(available));
        sendMessageToUnityGameObject("OnAdPlayable", message.toString());
    }

    public static void notifyAdStartToUnity(String placementId) {
        sendMessageToUnityGameObject("OnAdStart", placementId);
    }

    public static void notifyAdEndToUnity(String placementId)  throws JSONException{
        JSONObject message = new JSONObject();
        message.put("placementID", placementId);
        sendMessageToUnityGameObject("OnAdEnd", message.toString());
    }

    public static void notifyAdEndToUnity(String placementId, boolean success, boolean isCTAClicked)  throws JSONException{
        JSONObject message = new JSONObject();
        message.put("placementID", placementId);
        message.put("wasSuccessfulView", Boolean.valueOf(success));
        message.put("wasCallToActionClicked", Boolean.valueOf(isCTAClicked));
        sendMessageToUnityGameObject("OnAdEnd", message.toString());
    }

    public static void notifyAdClickToUnity(String placementId)  throws JSONException{
        JSONObject message = new JSONObject();
        message.put("placementID", placementId);
        sendMessageToUnityGameObject("OnAdClick", message.toString());
    }

    public static void notifyAdRewardedToUnity(String placementId)  throws JSONException{
        JSONObject message = new JSONObject();
        message.put("placementID", placementId);
        sendMessageToUnityGameObject("OnAdRewarded", message.toString());
    }

    public static void notifyAdLeftApplicationToUnity(String placementId)  throws JSONException{
        JSONObject message = new JSONObject();
        message.put("placementID", placementId);
        sendMessageToUnityGameObject("OnAdLeftApplication", message.toString());
    }

    public static void notifyAdViewedToUnity(String placementId)  throws JSONException {
        JSONObject message = new JSONObject();
        message.put("placementID", placementId);
        sendMessageToUnityGameObject("OnAdViewed", message.toString());
    }

    public static void runSafelyOnUiThread(Activity context, final Runnable runnable) {
        context.runOnUiThread(new Runnable() { // from class: com.vungle.androidplugin.Utility.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    Utility.LogMessage(LogLevel.Error, "RunSafelyOnUiThread: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public static float convertDpToPixel(Context context, float dp) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / 160.0f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getAndroidPositionFromUnityPosition(int adUnityPosition) {
        switch (adUnityPosition) {

            case 1:
                return 49;
            case 2:
                return 53;
            case 3:
                return 17;
            case 4:
                return 83;
            case 5:
                return 81;
            case 6:
                return 85;
            default:
                return 51;
        }
    }

    static void LogMessage(LogLevel logLevel, String message) {
        switch (logLevel) {
            case Error:
                Log.e(LOG_TAG, message);
                return;
            case Warning:
                Log.w(LOG_TAG, message);
                return;
            case Info:
                Log.i(LOG_TAG, message);
                return;
            case Debug:
                Log.d(LOG_TAG, message);
                return;
            case Verbose:
                Log.v(LOG_TAG, message);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getWidth(int bannerSize) {
        switch (bannerSize) {

            case 1:
                return 300;
            case 2:
                return 300;
            case 3:
                return 728;
            default:
                return 320;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getHeight(int bannerSize) {
        switch (bannerSize) {

            case 1:
                return 50;
            case 2:
                return 250;
            case 3:
                return 90;
            default:
                return 50;
        }
    }
}