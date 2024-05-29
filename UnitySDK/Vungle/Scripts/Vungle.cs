using System;
using System.Collections.Generic;

public static partial class Vungle
{
	public const string PluginVersion = "6.11.0.1";

	public enum Consent
	{
		Undefined = 0,
		Accepted = 1,
		Denied = 2,
	}

	public enum VungleBannerPosition
	{
		TopLeft = 0,
		TopCenter=1,
		TopRight = 2,
		Centered = 3,
		BottomLeft = 4,
		BottomCenter = 5,
		BottomRight = 6,
	}

	public enum VungleBannerSize
	{
		VungleAdSizeBanner = 0,         // width = 320.0f, .height = 50.0f
		VungleAdSizeBannerShort,        // width = 300.0f, .height = 50.0f
		VungleAdSizeBannerMedium,       // width = 300.0f, .height = 250.0f
		VungleAdSizeBannerLeaderboard,  // width = 728.0f, .height = 90.0f
	}

	public enum AppTrackingStatus
	{
		NOT_DETERMINED = 0,
		RESTRICTED = 1,
		DENIED = 2,
		AUTHORIZED = 3,
	}

	static IVungleHelper helper;

	// Fired when a Vungle SDK initialized
	public static Action onInitializeEvent;

	// Fired when a Vungle ad is ready to be displayed
	public static Action<string, bool> adPlayableEvent;

	// Fired when the video is shown
	public static Action<string> onAdStartedEvent;

	// Fired when a Vungle ad finished and provides the entire information about this event.
#if UNITY_ANDROID || UNITY_IOS
	[Obsolete("onAdFinishedEvent is a deprecated callback. Please use onAdEndEvent, onAdClickEvent, and onAdRewardedEvent")]
#endif
	public static Action<string, AdFinishedEventArgs> onAdFinishedEvent;

	// Fired when a Vungle ad finished (implemented for Android / iOS)
	public static Action<string> onAdEndEvent;

	// Fired when the ad is tapped (implemented for Android / iOS)
	public static Action<string> onAdClickEvent;

	// Fired when the user should be rewarded (implemented for Android / iOS)
	// Equivalent to onAdFinishedEvent's isCompletedView
	public static Action<string> onAdRewardedEvent;

	// Fired when the user taps the ad to leave the application (implemented for Android / iOS)
	public static Action<string> onAdLeftApplicationEvent;

	// Fired log event from sdk.
	public static Action<string> onLogEvent;

	// Fired when a Vungle Placement Prepared (implemented only for iOS)
	public static Action<string, string> onPlacementPreparedEvent;

	// Fired when a Vungle Creative fired (implemented only for iOS)
	public static Action<string, string> onVungleCreativeEvent;

	// Fired when the error is thrown
	public static Action<string> onErrorEvent;

	// Fired when the warning is thrown
	public static Action<string> onWarningEvent;

	// Fired when the App Tracking callback is fired (implemented only for iOS)
	public static Action<AppTrackingStatus> onAppTrackingEvent;

	public static long? minimumDiskSpaceForInitialization;
	public static long? minimumDiskSpaceForAd;
	public static bool? enableHardwareIdPrivacy;

	public static VungleLog.Level logLevel { get; set; }

	static Vungle()
	{
#if UNITY_EDITOR
		helper = new VungleUnityEditor();
#elif UNITY_IOS
		helper = new VungleiOS();
#elif UNITY_ANDROID
		helper = new VungleAndroid();
#elif UNITY_WSA_10_0 || UNITY_WINRT_8_1 || UNITY_METRO
		helper = new VungleWindows();
#endif

		logLevel = VungleLog.Level.Debug;

		VungleManager.noop();
	}

	#region SDKSetup

	// Used for iOS 14 for requesting permission for the IDFA
	// Requesting for the IDFA will allow targeted ads
	public static void RequestTrackingAuthorization()
	{
		try
		{
			helper.RequestTrackingAuthorization();
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "RequestTrackingAuthorization", "Vungle.RequestTrackingAuthorization", e.ToString());
		}
	}

	public static void SetMinimumDiskSpaceForInitialization(long minimumDiskSpace)
	{
		minimumDiskSpaceForInitialization = minimumDiskSpace;
	}

	public static void SetMinimumDiskSpaceForAd(long minimumDiskSpace)
	{
		minimumDiskSpaceForAd = minimumDiskSpace;
	}

	public static void EnableHardwareIdPrivacy(bool dontSendHardwareId)
	{
		enableHardwareIdPrivacy = dontSendHardwareId;
	}

	public static void updateConsentStatus(Consent consent, string version = "1.0")
	{
		try
		{
			helper.UpdateConsentStatus(consent, version);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "updateConsentStatus", "Vungle.updateConsentStatus", e.ToString());
		}
	}

	public static Consent getConsentStatus()
	{
		try
		{
			return helper.GetConsentStatus();
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "getConsentStatus", "Vungle.getConsentStatus", e.ToString());
			return Consent.Undefined;
		}
	}

	public static void updateCCPAStatus(Consent consent)
	{
		try
		{
			helper.UpdateCCPAStatus(consent);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "updateCCPAStatus", "Vungle.updateCCPAStatus", e.ToString());
		}
	}

	public static Consent getCCPAStatus()
	{
		try
		{
			return helper.GetCCPAStatus();
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "getCCPAStatus", "Vungle.getCCPAStatus", e.ToString());
			return Consent.Undefined;
		}
	}

	public static void updateCoppaStatus(bool isUserCoppa)
	{
		try
		{
			helper.UpdateCoppaStatus(isUserCoppa);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "updateCoppaStatus", "Vungle.updateCoppaStatus", e.ToString());
		}
	}
	#endregion

	#region SDKInitialization

	public static void init(string appId)
	{
		try
		{
			helper.Init(appId);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "init", "Vungle.init", e.ToString());
		}
	}

	[Obsolete("This method is no longer supported. Please use init(appId).")]
	public static void init(string appId, bool initHeaderBiddingDelegate)
	{
		try
		{
			helper.Init(appId);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "init", "Vungle.init", e.ToString());
		}
	}

	public static bool isInitialized()
	{
		try
		{
			return helper.IsInitialized();
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "isInitialized", "Vungle.isInitialized", e.ToString());
			return false;
		}
	}

	#endregion

	#region AdLifeCycle

	public static bool isAdvertAvailable(string placementId)
	{
		try
		{
			return helper.IsAdvertAvailable(placementId);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "isAdvertAvailable", "Vungle.isAdvertAvailable", e.ToString());
			return false;
		}
	}

	public static void loadAd(string placementId)
	{
		try
		{
			helper.LoadAd(placementId);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "loadAd", "Vungle.loadAd", e.ToString());
		}
	}

	public static void playAd(string placementId)
	{
		try
		{
			helper.PlayAd(placementId);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "playAd", "Vungle.playAd", e.ToString());
		}
	}

	public static void playAd(Dictionary<string, object> options, string placementId)
	{
		if (options == null)
		{
			options = new Dictionary<string, object>();
		}

		try
		{
			helper.PlayAd(options, placementId);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "playAd", "Vungle.playAd", e.ToString());
		}
	}

	[Obsolete("closeAd was used for flex feed ads. The ad template is no longer supported.")]
	public static bool closeAd(string placementId)
	{
		return false;
	}

	#endregion

	#region AdLifeCycle - Banner

	public static bool isAdvertAvailable(string placementId, VungleBannerSize adSize)
	{
		try
		{
			return helper.IsAdvertAvailable(placementId, adSize);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "isAdvertAvailable", "Vungle.isAdvertAvailable", e.ToString());
			return false;
		}
	}

	/**
	 * Load Banner of given AdSize and at given AdPosition
	 *
	 * placementId String
	 * adSize AdSize Size of the banner
	 * adPosition AdPosition Position of the Banner on screen
	*/
	public static void loadBanner(string placementId, VungleBannerSize adSize, VungleBannerPosition adPosition)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.LogEvent, "Vungle.loadBanner",
			GetLogMessage("loading Banner", placementId));
		try
		{
			helper.LoadBanner(placementId, adSize, adPosition);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "loadBanner", "Vungle.loadBanner", e.ToString());
		}
	}

	public static void setBannerOffset(string placementID, int x, int y)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.LogEvent, "Vungle.setOffset",
			GetLogMessage("setOffset", placementID));
		try
		{
			helper.SetBannerOffset(placementID, x, y);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "setOffset", "Vungle.setOffset", e.ToString());
		}
	}

	public static void showBanner(string placementId)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.LogEvent, "Vungle.showBanner",
			GetLogMessage("playing Banner", placementId));
		try
		{
			helper.ShowBanner(placementId);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "showBanner", "Vungle.showBanner", e.ToString());
		}
	}

	public static void showBanner(string placementId, Dictionary<string, object> options)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.LogEvent, "Vungle.showBanner",
			GetLogMessage("playing Banner", placementId));
		try
		{
			helper.ShowBanner(placementId, options);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "showBanner", "Vungle.showBanner", e.ToString());
		}
	}

	public static void closeBanner(string placementId)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.LogEvent, "Vungle.closeBanner",
			GetLogMessage("closing Banner", placementId));
		try
		{
			helper.CloseBanner(placementId);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "closeBanner", "Vungle.closeBanner", e.ToString());
		}
	}

	private static string GetLogMessage(string context, string placementId)
	{
		return string.Format("{0} {1}: {2} for placement ID: {3}", helper.VersionInfo, DateTime.Today.ToString(), context, placementId);
	}

	#endregion

	#region PlaybackOptions

	public static bool isSoundEnabled()
	{
		try
		{
			return helper.IsSoundEnabled();
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "IsSoundEnabled", "Vungle.IsSoundEnabled", e.ToString());
			return false;
		}
	}

	public static void setSoundEnabled(bool isEnabled)
	{
		try
		{
			helper.SetSoundEnabled(isEnabled);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "setSoundEnabled", "Vungle.setSoundEnabled", e.ToString());
		}
	}

	#endregion

	#region PauseAndResumeHandling

	public static void onResume()
	{
		try
		{
			helper.OnResume();
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "onResume", "Vungle.onResume", e.ToString());
		}
	}

	public static void onPause()
	{
		try
		{
			helper.OnPause();
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "onPause", "Vungle.onPause", e.ToString());
		}
	}
	#endregion

	#region TestUsage

	public static void setLogEnable(bool enable)
	{
		try
		{
			helper.SetLogEnable(enable);
		}
		catch (Exception e)
		{
			VungleLog.Log(VungleLog.Level.Error, "setLogEnable", "Vungle.setLogEnable", e.ToString());
		}
	}

	#endregion
}
