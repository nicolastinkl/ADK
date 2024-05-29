using System.Collections.Generic;

#if UNITY_EDITOR
public partial class VungleUnityEditor : IVungleHelper
{
	public string SdkVersion
	{
		get
		{
			return "Unity Editor Mode";
		}
	}

	public string VersionInfo
	{
		get
		{
			return "Unity Editor Mode";
		}
	}

	public void UpdateConsentStatus(Vungle.Consent consent, string version = "1.0") { return; }
	public Vungle.Consent GetConsentStatus() { return Vungle.Consent.Undefined; }

	public void UpdateCCPAStatus(Vungle.Consent consent) { return; }
	public Vungle.Consent GetCCPAStatus() { return Vungle.Consent.Undefined; }
	public void UpdateCoppaStatus(bool isUserCoppa) { }

	public void SetLogEnable(bool enable) { return; }
	public void Init(string appId) { return; }
	public void Init(string appId, bool initHeaderBiddingDelegate) { return; }
	public bool IsInitialized() { return false; }

	public bool IsAdvertAvailable(string placementID) { return false; }
	public void LoadAd(string placementID) { return; }
	public void PlayAd(string placementID) { return; }
	public void PlayAd(Dictionary<string, object> options, string placementID) { return; }
	public bool CloseAd(string placementID) { return false; }

	public bool IsAdvertAvailable(string placementID, Vungle.VungleBannerSize adSize) { return false; }
	public void LoadBanner(string placementID, Vungle.VungleBannerSize adSize, Vungle.VungleBannerPosition adPosition) { return; }
	public void SetBannerOffset(string placementID, int x, int y) { return; }
	public void ShowBanner(string placementID) { return; }
	public void ShowBanner(string placementID, Dictionary<string, object> options) { return; }
	public void CloseBanner(string placementID) { return; }

	public bool IsSoundEnabled() { return false; }
	public void SetSoundEnabled(bool isEnabled) { return; }

	public void OnResume() { return; }
	public void OnPause() { return; }

	public void RequestTrackingAuthorization() { }
}
#endif
