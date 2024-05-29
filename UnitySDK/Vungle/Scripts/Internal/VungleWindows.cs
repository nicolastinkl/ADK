using System;
using System.Collections.Generic;
using UnityEngine;

#if UNITY_WSA_10_0 || UNITY_WINRT_8_1 || UNITY_METRO
using VungleSDKProxy;

public enum VungleAdOrientation
{
	Portrait = 1,
	Landscape = 5,
	All = 6,
	AutoRotate = 6,
	MatchVideo = 6,
}

public class OptionConstants
{
	public const string UserTag = "userTag";
	public const string Orientation = "orientation";
	public const string AlertText = "alertText";
	public const string AlertTitle = "alertTitle";
	public const string CloseText = "closeText";
	public const string ContinueText = "continueText";
	public const string BackImmediately = "backImmediately";
	public const string FlexCloseSec = "flexCloseSec";
}

public partial class VungleWindows : IVungleHelper
{
	private VunglePlugin plugin;
	private bool isSoundEnabled = true;

	public string SdkVersion
	{
		get
		{
			return VunglePlugin.GetSdkVersion();
		}
	}

	public string VersionInfo
	{
		get
		{
			return string.Format("VungleSDKCall-UWP/{0}-{1}", Vungle.PluginVersion, SdkVersion);
		}
	}

	#region SDKSetup

	public void RequestTrackingAuthorization() { }

	public void UpdateConsentStatus(Vungle.Consent consent, string version = "1.0")
	{
		if (Vungle.Consent.Undefined == consent) { return; }
		if (plugin == null)
		{
			VungleLog.Log(VungleLog.Level.Warning, "GetConsentStatus", "VungleWindows.GetConsentStatus", "The plugin has not been initialized yet.");
			return;
		}

		plugin.UpdateConsentStatus((Vungle.Consent.Accepted == consent) ? Consent.Accepted : Consent.Denied, version);
	}

	public Vungle.Consent GetConsentStatus()
	{
		if (plugin == null)
		{
			VungleLog.Log(VungleLog.Level.Warning, "GetConsentStatus", "VungleWindows.GetConsentStatus", "The plugin has not been initialized yet.");
			return Vungle.Consent.Undefined;
		}
		return (Vungle.Consent)plugin.GetConsentStatus();
	}

	public void UpdateCCPAStatus(Vungle.Consent status)
	{
		VunglePlugin.UpdateCCPAStatus((int)status);
	}

	public Vungle.Consent GetCCPAStatus()
	{
		int status = VunglePlugin.GetCCPAStatus();
		switch (status)
		{
			case 2:
				return Vungle.Consent.Denied;
			case 1:
				return Vungle.Consent.Accepted;
			default:
				return Vungle.Consent.Undefined;
		}
	}

	public void UpdateCoppaStatus(bool isUserCoppa) {
		VunglePlugin.UpdateCoppaStatus(isUserCoppa);
	}

	#endregion

	#region SDKInitialization

	public void Init(string appId)
	{
		Initialize(appId);
	}

	public void Init(string appId, bool initHeaderBiddingDelegate)
	{
		Initialize(appId);
	}

	// Starts up the SDK with the given appId
	private void Initialize(string appId)
	{
		VungleLog.Log(VungleLog.Level.Debug, "Initialization", "VungleWindows.Initialize", "Plugin: initializing loom");
		VungleSceneLoom.Initialize();

		VunglePlugin.AddInitCallback(VungleManager.Instance.OnInitialize);
		VunglePlugin.AddAdPlayableCallback(VungleManager.Instance.OnAdPlayable);
		VunglePlugin.AddAdStartCallback(VungleManager.Instance.OnAdStart);
		VunglePlugin.AddAdEndCallback(VungleManager.Instance.OnAdEnd);
		VunglePlugin.AddLogCallback(VungleManager.Instance.OnSDKLog);
		VunglePlugin.AddWarningCallback(VungleManager.Instance.OnWarning);
		VunglePlugin.AddErrorCallback(VungleManager.Instance.OnError);

		InitializationConfig config = new InitializationConfig();
		if (Vungle.minimumDiskSpaceForInitialization.HasValue)
		{
			config.SetMinimumDiskSpaceForInit(Vungle.minimumDiskSpaceForInitialization.Value);
		}
		if (Vungle.minimumDiskSpaceForAd.HasValue)
		{
			config.SetMinimumDiskSpaceForAd(Vungle.minimumDiskSpaceForAd.Value);
		}
		if (Vungle.enableHardwareIdPrivacy.HasValue)
		{
			config.SetDisableAshwidTracking(Vungle.enableHardwareIdPrivacy.Value);
		}
		config.SetPluginVersion(Vungle.PluginVersion);
		VungleLog.Log(VungleLog.Level.Debug, "Initialization", "VungleWindows.Initialize", "Plugin: initializing Bridge");
		plugin = VunglePlugin.Initialize(appId, config);
	}

	public bool IsInitialized()
	{
		return VunglePlugin.IsInitialized();
	}

	#endregion

	#region AdLifeCycle

	public bool IsAdvertAvailable(string placementId)
	{
		VungleLog.Log(VungleLog.Level.Debug, "FullScreenAd", "VungleWindows.IsAdvertAvailable", string.Format("Plugin: Checking if placement {0} is available", placementId));
		return FullScreenAd.IsAdPlayable(placementId);
	}

	public void LoadAd(string placementId)
	{
		VungleLog.Log(VungleLog.Level.Debug, "FullScreenAd", "VungleWindows.LoadAd", string.Format("Plugin: Loading placement {0}", placementId));
		FullScreenAd.Load(placementId);
	}

	public void PlayAd(string placementId)
	{
		VungleLog.Log(VungleLog.Level.Debug, "FullScreenAd", "VungleWindows.PlayAd", string.Format("Plugin: Playing placement {0}", placementId));
		AdConfig cfg = new AdConfig();
		cfg.SetUserId(string.Empty);
		cfg.SetSoundEnabled(this.isSoundEnabled);
		cfg.SetOrientation(DisplayOrientations.AutoRotate);
		FullScreenAd.Play(placementId, cfg);
	}

	public void PlayAd(Dictionary<string, object> options, string placementId)
	{
		VungleLog.Log(VungleLog.Level.Debug, "FullScreenAd", "VungleWindows.PlayAd", string.Format("Plugin: Playing placement {0} with custom options", placementId));
		if (options == null)
		{
			options = new Dictionary<string, object>();
		}
		AdConfig cfg = new AdConfig();
		SetAdConfig(cfg, options);
		FullScreenAd.Play(placementId, cfg);
	}

	private void SetAdConfig(AdConfig config, Dictionary<string, object> options)
	{
		if (config == null || options == null) { return; }

		config.SetSoundEnabled(this.isSoundEnabled);
		SetValue<string>(options, OptionConstants.UserTag, config.SetUserId);
		SetValue<string>(options, OptionConstants.AlertText, config.SetIncentivizedDialogBody);
		SetValue<string>(options, OptionConstants.AlertTitle, config.SetIncentivizedDialogTitle);
		SetValue<string>(options, OptionConstants.CloseText, config.SetIncentivizedDialogCloseButton);
		SetValue<string>(options, OptionConstants.ContinueText, config.SetIncentivizedDialogContinueButton);
		SetValue<bool>(options, OptionConstants.BackImmediately, config.SetBackButtonImmediatelyEnabled);

		if (options.ContainsKey(OptionConstants.Orientation))
		{
			// Legacy implementation
			// If it is true, then it means Landscape
			// If it is false, then it means AutoRotate
			if (options[OptionConstants.Orientation] is bool)
			{
				bool orientation = (bool)options[OptionConstants.Orientation];
				if (orientation)
				{
					config.SetOrientation(DisplayOrientations.Landscape);
				}
				else
				{
					config.SetOrientation(DisplayOrientations.AutoRotate);
				}
			}
			else if (options[OptionConstants.Orientation] is int)
			{
				int orientation = (int)options[OptionConstants.Orientation];
				switch (orientation)
				{
					case 1:
						config.SetOrientation(DisplayOrientations.Portrait);
						break;
					case 5:
						config.SetOrientation(DisplayOrientations.Landscape);
						break;
					default:
						config.SetOrientation(DisplayOrientations.AutoRotate);
						break;
				}
			}
			else
			{
				config.SetOrientation(DisplayOrientations.AutoRotate);
			}
		}
		else
		{
			// default to autorotate
			config.SetOrientation(DisplayOrientations.AutoRotate);
		}
	}

	private void SetValue<T>(Dictionary<string, object> options, string key, Action<T> callback)
	{
		if (options != null && !string.IsNullOrEmpty(key) &&
			options.ContainsKey(key) && options[key] is T)
		{
			if (callback != null)
			{
				callback((T)options[key]);
			}
		}
	}

	private delegate U CalculateValue<T, U>(T obj);
	private void SetValue<T, U>(Dictionary<string, object> options, string key, Action<U> callback, CalculateValue<T, U> valueCallback)
	{
		if (options != null && !string.IsNullOrEmpty(key) &&
			options.ContainsKey(key) && options[key] is T)
		{
			if (callback != null && valueCallback != null)
			{
				callback(valueCallback((T)options[key]));
			}
		}
	}

	#endregion

	#region AdLifeCycle - Banner

	public bool IsAdvertAvailable(string placementId, Vungle.VungleBannerSize adSize)
	{
		VungleLog.Log(VungleLog.Level.Debug, "BannerAd", "VungleWindows.IsAdvertAvailable", string.Format("Plugin: Checking if placement {0} is available", placementId));
		return BannerAd.IsAdPlayable(placementId, (int)adSize + 1);
	}

	/**
     * Load Banner of given AdSize and at given AdPosition
     *
     * placementId String
     * adSize AdSize Size of the banner
     * adPosition AdPosition Position of the Banner on screen
     */

	public void LoadBanner(string placementId, Vungle.VungleBannerSize adSize, Vungle.VungleBannerPosition adPosition)
	{
#if !UNITY_2019_1 && !UNITY_2019_2
		SetBannerGrid(delegate
		{
			VungleLog.Log(VungleLog.Level.Debug, "BannerAd", "VungleWindows.LoadBanner", string.Format("Plugin: Loading placement {0}", placementId));
			BannerAd.Load(placementId, (int)adSize + 1, (int)adPosition);
		});
#else
		VungleLog.Log(VungleLog.Level.Error, VungleLog.Context.LogEvent, "VungleWindows.LoadBanner",
			"2019.1 and 2019.2 crashes when instantiating a core class for banners, but it is fixed in 2019.3");
#endif
	}

	private void SetBannerGrid(Action callback)
	{
		if (plugin == null)
		{
			return;
		}
		if (!plugin.IsParentGridSet())
		{
			VungleLog.Log(VungleLog.Level.Debug, "BannerAd", "VungleWindows.SetBannerGrid", "Retrieving the Grid panel for banners");
			VungleSceneLoom.Instance.GetSwapChainPanel((object obj) =>
			{
				plugin.SetParentGrid(obj);
				if (callback != null)
				{
					callback();
				}
			});
		}
		else
		{
			if (callback != null)
			{
				callback();
			}
		}
	}

	public void SetBannerOffset(string placementID, int x, int y)
	{
		BannerAd.SetOffset(placementID, x, y);
	}

	/**
	 * Play Banner with given placementId
	 *
	 * placementId String
     */
	public void ShowBanner(string placementId)
	{
#if !UNITY_2019_1 && !UNITY_2019_2
		SetBannerGrid(delegate
		{
			VungleLog.Log(VungleLog.Level.Debug, "BannerAd", "VungleWindows.ShowBanner", string.Format("Plugin: Playing placement {0}", placementId));
			BannerAd.Play(placementId);
		});
#else
		VungleLog.Log(VungleLog.Level.Error, VungleLog.Context.LogEvent, "VungleWindows.ShowBanner",
			"2019.1 and 2019.2 crashes when instantiating a core class for banners, but it is fixed in 2019.3");
#endif
	}

	public void ShowBanner(string placementId, Dictionary<string, object> options)
	{
#if !UNITY_2019_1 && !UNITY_2019_2
		SetBannerGrid(delegate
		{
			VungleLog.Log(VungleLog.Level.Debug, "BannerAd", "VungleWindows.ShowBanner", string.Format("Plugin: Playing placement {0}", placementId));
			BannerConfig cfg = new BannerConfig();
			cfg.SetSoundEnabled(this.isSoundEnabled);
			BannerAd.Play(placementId, cfg);
		});
#else
		VungleLog.Log(VungleLog.Level.Error, VungleLog.Context.LogEvent, "VungleWindows.ShowBanner",
			"2019.1 and 2019.2 crashes when instantiating a core class for banners, but it is fixed in 2019.3");
#endif
	}

	/**
	 * Close Banner with given placementId
	 *
	 * placementId String
	 */
	public void CloseBanner(string placementId)
	{
#if !UNITY_2019_1 && !UNITY_2019_2
		VungleLog.Log(VungleLog.Level.Debug, "BannerAd", "VungleWindows.CloseBanner", string.Format("Plugin: Closing placement {0}", placementId));
		BannerAd.Close(placementId);
#else
		VungleLog.Log(VungleLog.Level.Error, VungleLog.Context.LogEvent, "VungleWindows.CloseBanner",
			"2019.1 and 2019.2 crashes when instantiating a core class for banners, but it is fixed in 2019.3");
#endif
	}

	#endregion

	#region PlaybackOptions

	public bool IsSoundEnabled()
	{
		return this.isSoundEnabled;
	}

	public void SetSoundEnabled(bool isEnabled)
	{
		this.isSoundEnabled = isEnabled;
	}

	#endregion

	#region PauseAndResumeHandling

	public void OnResume()
	{
		return;
	}

	public void OnPause()
	{
		return;
	}

	#endregion

	#region TestUsage

	public void SetLogEnable(bool enable)
	{
		VunglePlugin.SetLogEnabled(enable);
	}

	#endregion
}
#endif
