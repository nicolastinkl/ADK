using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;

public class VungleManager : MonoBehaviour
{
	private static VungleManager instance;
	public static VungleManager Instance
	{
		get
		{
			return instance;
		}
	}

	#region Constructor and Lifecycle
	static VungleManager()
	{
		// try/catch this so that we can warn users if they try to stick this script on a GO manually
		try
		{
			// create a new GO for our manager
			var go = new GameObject("VungleManager");
			instance = go.AddComponent<VungleManager>();
			DontDestroyOnLoad(go);
		}
		catch
		{
			VungleLog.Log(VungleLog.Level.Error, VungleLog.Context.SDKInitialization, "VungleManager",
				"Vungle.cs will create the VungleManager instance. A Vungle manager already exists in your scene. Please remove the script from the scene.");
		}
	}

	// used to ensure the VungleManager will always be in the scene to avoid SendMessage logs if the user isn't using any events
	public static void noop() { }

	#endregion

	#region Android, iOS, UWP callbacks

	public void OnInitialize(string arg)
	{
		if ("1".Equals(arg))
		{
			Initialize();
		}
	}

	public void OnAdPlayable(string json)
	{
		Dictionary<string, object> attrs = (Dictionary<string, object>)MiniJSONV.Json.Deserialize(json);
		bool isAdAvailable = ExtractBoolValue(attrs, "isAdAvailable");
		string placementID = attrs["placementID"].ToString();
		AdPlayable(placementID, isAdAvailable);
	}

	public void OnAdStart(string placementID)
	{
		AdStarted(placementID);
	}

	public void OnAdEnd(string json)
	{
		Dictionary<string, object> attrs = (Dictionary<string, object>)MiniJSONV.Json.Deserialize(json);
#if UNITY_ANDROID
		string ctaAction = "wasCallToActionClicked";
		// if the user watched the full video
		string completedView = "wasSuccessfulView";
#elif UNITY_IOS
		string ctaAction = "didDownload";
		string completedView = "completedView";
#else
		string ctaAction = "wasCallToActionClicked";
		string completedView = "completedView";
#endif
		string timeWatched = "playTime"; // for iOS, UWP only
		string placementId = "placementID";

		// if the message contains the ctaAction or completedView, then it is a legacy action
		if (attrs.ContainsKey(ctaAction))
		{
			AdFinishedEventArgs args = new AdFinishedEventArgs();
			args.WasCallToActionClicked = ExtractBoolValue(attrs, ctaAction);
			args.IsCompletedView = ExtractBoolValue(attrs, completedView);
			args.TimeWatched = ExtractDoubleValue(attrs, timeWatched);
			AdFinished(ExtractStringValue(attrs, placementId), args);
		}
		else
		{
			AdFinished(ExtractStringValue(attrs, placementId));
		}
	}

	public void OnAdClick(string json)
	{
		if (Vungle.onAdClickEvent != null)
		{
			Dictionary<string, object> attrs = (Dictionary<string, object>)MiniJSONV.Json.Deserialize(json);
			string placementId = ExtractStringValue(attrs, "placementID");
			VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.AdLifecycle, "OnAdClick",
				string.Format("An ad was clicked for {0}", placementId));
			Vungle.onAdClickEvent(placementId);
		}
	}

	public void OnAdRewarded(string json)
	{
		Dictionary<string, object> attrs = (Dictionary<string, object>)MiniJSONV.Json.Deserialize(json);
		string placementId = ExtractStringValue(attrs, "placementID");
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.AdLifecycle, "OnAdRewarded",
			string.Format("An ad was completed for {0}", placementId));
		if (Vungle.onAdRewardedEvent != null)
		{
			Vungle.onAdRewardedEvent(placementId);
		}
	}

	public void OnPlacementPrepared(string json)
	{
		Dictionary<string, object> attrs = (Dictionary<string, object>)MiniJSONV.Json.Deserialize(json);
		string placementID = ExtractStringValue(attrs, "placementID");
		string bidToken = ExtractStringValue(attrs, "bidToken");
		PlacementPrepared(placementID, bidToken);
	}

	public void OnVungleCreative(string json)
	{
		Dictionary<string, object> attrs = (Dictionary<string, object>)MiniJSONV.Json.Deserialize(json);
		string placementID = ExtractStringValue(attrs, "placementID");
		string creativeID = ExtractStringValue(attrs, "creativeID");
		VungleCreative(placementID, creativeID);
	}

	public void TrackingCallback(string trackingValue)
	{
		int res = 0;
		if (!Int32.TryParse(trackingValue, out res))
		{
			VungleLog.Log(VungleLog.Level.Error, "App Tracking Callback",
				"TrackingCallback", "Failed to get a valid return value from the App Tracking Transparency callback.");
			return;
		}

		VungleLog.Log(VungleLog.Level.Debug, "App Tracking Callback",
			"TrackingCallback", string.Format("The result of the tracking callback {0}", trackingValue));

		if (Vungle.onAppTrackingEvent == null)
		{
			return;
		}

		// 0 - Not Determined
		// 1 - Restricted
		// 2 - Denied
		// 3 - Authorized
		switch (res)
		{
			case 1:
				// Access to app-related data for tracking is restricted
				// User cannot change the setting
				Vungle.onAppTrackingEvent(Vungle.AppTrackingStatus.RESTRICTED);
				break;
			case 2:
				// The user denied access to app-related data for tracking
				// The user needs to go to settings to allow tracking
				Vungle.onAppTrackingEvent(Vungle.AppTrackingStatus.DENIED);
				break;
			case 3:
				// The user authorized access to app-related data for tracking
				Vungle.onAppTrackingEvent(Vungle.AppTrackingStatus.AUTHORIZED);
				break;
			case 0:
			default:
				// Tracking dialog has not been shown
				Vungle.onAppTrackingEvent(Vungle.AppTrackingStatus.NOT_DETERMINED);
				break;
		}
	}

	public void OnSDKLog(string log)
	{
		Log(log);
	}

	public void OnWarning(string warning)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.LogEvent, "SendOnWarningEvent",
			"Vungle.onWarning => From Unity:" + warning);
		if (Vungle.onWarningEvent != null)
		{
			Vungle.onWarningEvent(warning);
		}
	}

	public void OnError(string error)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.LogEvent, "SendOnErrorEvent",
			"Vungle.onError => From Unity:" + error);
		if (Vungle.onErrorEvent != null)
		{
			Vungle.onErrorEvent(error);
		}
	}

	public void OnAdLeftApplication(string json)
	{
		Dictionary<string, object> attrs = (Dictionary<string, object>)MiniJSONV.Json.Deserialize(json);
		string placementId = ExtractStringValue(attrs, "placementID");
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.AdLifecycle, "OnAdLeftApplication",
			string.Format("Leaving application for {0}", placementId));
		if (Vungle.onAdLeftApplicationEvent != null)
		{
			Vungle.onAdLeftApplicationEvent(placementId);
		}
	}

	#endregion

	#region EventHandling
	private void Initialize()
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.SDKInitialization, "SendOnInitializeEvent", "SDK is initialized");
		if (Vungle.onInitializeEvent != null)
		{
#if UNITY_WSA_10_0 || UNITY_WINRT_8_1 || UNITY_METRO
			VungleSceneLoom.Loom.QueueOnMainThread(() =>
			{
				Vungle.onInitializeEvent();
			});
#else
			Vungle.onInitializeEvent();
#endif
		}
	}

	private void AdPlayable(string placementId, bool playable)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.AdLifecycle, "SendAdPlayableEvent",
			string.Format("Placement {0} - Playable status {1}", placementId, playable.ToString()));
		if (Vungle.adPlayableEvent != null)
		{
#if UNITY_WSA_10_0 || UNITY_WINRT_8_1 || UNITY_METRO
			VungleSceneLoom.Loom.QueueOnMainThread(() =>
			{
				Vungle.adPlayableEvent(placementId, playable);
			});
#else
			Vungle.adPlayableEvent(placementId, playable);
#endif
		}
	}

	private void AdStarted(string placementId)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.AdLifecycle, "SendOnAdStartedEvent",
			string.Format("An ad started displaying for {0}", placementId));
		if (Vungle.onAdStartedEvent != null)
		{
#if UNITY_WSA_10_0 || UNITY_WINRT_8_1 || UNITY_METRO
			VungleSceneLoom.Loom.QueueOnMainThread(() =>
			{
				Vungle.onAdStartedEvent(placementId);
			});
#else
			Vungle.onAdStartedEvent(placementId);
#endif
		}
	}

	private void AdFinished(string placementId)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.AdLifecycle, "SendOnAdFinishedEvent",
			string.Format("An ad finished displaying for {0}", placementId));
		if (Vungle.onAdEndEvent != null)
		{
#if UNITY_WSA_10_0 || UNITY_WINRT_8_1 || UNITY_METRO
			VungleSceneLoom.Loom.QueueOnMainThread(() =>
			{
				Vungle.onAdEndEvent(placementId);
			});
#else
			Vungle.onAdEndEvent(placementId);
#endif
		}
	}

#pragma warning disable 0618
	private void AdFinished(string placementId, AdFinishedEventArgs args)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.AdLifecycle, "SendOnAdFinishedEvent",
			string.Format("An ad finished displaying for {0}", placementId));
		if (Vungle.onAdFinishedEvent != null)
		{
#if UNITY_WSA_10_0 || UNITY_WINRT_8_1 || UNITY_METRO
			VungleSceneLoom.Loom.QueueOnMainThread(() =>
			{
				Vungle.onAdFinishedEvent(placementId, args);
			});
#else
			Vungle.onAdFinishedEvent(placementId, args);
#endif
		}
	}
#pragma warning restore 0618

	private void PlacementPrepared(string placementId, string bidToken)
	{
		if (Vungle.onPlacementPreparedEvent != null)
		{
			Vungle.onPlacementPreparedEvent(placementId, bidToken);
		}
	}

	private void VungleCreative(string placementId, string creativeID)
	{
		if (Vungle.onVungleCreativeEvent != null)
		{
			Vungle.onVungleCreativeEvent(placementId, creativeID);
		}
	}

	private void Log(string log)
	{
		VungleLog.Log(VungleLog.Level.Debug, VungleLog.Context.LogEvent, "SendOnLogEvent", "An log event. Log message is' " + log + "'.");
		if (Vungle.onLogEvent != null)
		{
#if UNITY_WSA_10_0 || UNITY_WINRT_8_1 || UNITY_METRO
			VungleSceneLoom.Loom.QueueOnMainThread(() =>
			{
				Vungle.onLogEvent(log);
			});
#else
			Vungle.onLogEvent(log);
#endif
		}
	}

	#endregion

	#region util methods
	private bool ExtractBoolValue(Dictionary<string, object> attrs, string key)
	{
		object val = null;
		if (attrs.TryGetValue(key, out val))
		{
			return bool.Parse(val.ToString());
		}
		return false;
	}

	private string ExtractStringValue(Dictionary<string, object> attrs, string key)
	{
		object val = null;
		if (attrs.TryGetValue(key, out val))
		{
			return val.ToString();
		}
		return string.Empty;
	}

	private double ExtractDoubleValue(Dictionary<string, object> attrs, string key)
	{
		object val = null;
		if (attrs.TryGetValue(key, out val))
		{
			return double.Parse(val.ToString());
		}
		return 0;
	}
	#endregion

	private void OnApplicationPause(bool pauseStatus)
	{
		if (pauseStatus)
		{
			Vungle.onPause();
		}
		else
		{
			Vungle.onResume();
		}
	}
}
