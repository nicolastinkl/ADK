package com.vungle.androidplugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.mobilefuse.sdk.MobileFuse;
import com.mobilefuse.sdk.privacy.MobileFusePrivacyPreferences;
import com.unity3d.player.UnityPlayerActivity;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.unity3d.player.UnityPlayer;
import com.vungle.ads.BannerAd;
import com.vungle.ads.BannerAdListener;
import com.vungle.ads.BannerAdSize;
import com.vungle.ads.BannerView;
import com.vungle.ads.BaseAd;
import com.vungle.ads.InitializationListener;
import com.vungle.ads.VungleAds;
import com.vungle.ads.VungleError;
import com.vungle.ads.internal.network.VungleApiClient;

/* loaded from: vunglePlugin.aar:classes.jar:com/vungle/androidplugin/VunglePlugin.class */
public class VunglePlugin implements BannerAdListener {
    private static VunglePlugin instance;
    private Set<String> autoCachedPlacements = new HashSet();
    private long m_minimumSpaceForInit = -1;
    private long m_minimumSpaceForAd = -1;
    private Boolean m_disableHardwareId = null;

    private BannerAd bannerAd = null;
    private int bannerGravityAsInt = Gravity.CENTER_VERTICAL;
    protected static final String LOG_TAG = VunglePlugin.class.getSimpleName();
    protected static boolean isSoundEnabled = true;
    private static Map<String, IVungleAd> vungleAds = new HashMap();

    public static Activity getActivity() {
        return UnityPlayer.currentActivity;
    }

    public static VunglePlugin instance() {
        if (instance == null) {
            instance = new VunglePlugin();
        }
        return instance;
    }

    public void setCustomEnvironment(String environment) {
        try {

            Field field = VungleApiClient.class.getDeclaredField("BASE_URL");
            field.setAccessible(true);
            field.set(null, environment);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
        }
    }

    public String getEnvironment() {
        try {
            Field field = VungleApiClient.class.getDeclaredField("BASE_URL");
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    @SuppressLint({ "DefaultLocale" })
    public void init(final String appId, final String pluginVersion) {
        // String newappId = "655c26e246464d20f199afa5";
        Utility.runSafelyOnUiThread(getActivity(), new Runnable() { // from class:
                                                                    // com.vungle.androidplugin.VunglePlugin.1
            @Override // java.lang.Runnable
            public void run() {
                /*
                 * VungleSettings settings = VunglePlugin.this.GetDefaultEmptySettings();
                 * if (VunglePlugin.this.haveExtraSettings()) {
                 * settings = VunglePlugin.this.GetInitializationSettings();
                 * }
                 * Plugin.addWrapperInfo(VungleApiClient.WrapperFramework.unity, pluginVersion);
                 */
                VungleAds.init(VunglePlugin.getActivity().getApplicationContext(), appId, new InitializationListener() {
                    @Override
                    public void onSuccess() {

                        Utility.notifyInitToUnity(true);
                        /// loadBanner("BANNER_NON_BIDDING-4570799",1,1);

                    }

                    @Override
                    public void onError(@NonNull VungleError vungleError) {
                        // Log.e("onError",""+vungleError.getErrorMessage());
                        Utility.notifyErrorToUnity(String.format("Initialization failure: %s", vungleError.toString()));
                        Utility.notifyInitToUnity(false);
                    }
                });

                MobileFusePrivacyPreferences privacyPrefs = new MobileFusePrivacyPreferences.Builder()
                        .setGppConsentString("DBACNYA~CPXxRfAPXxRfAAfKABENB-CgAAAAAAAAAAYgAAAAAAAA~1YNN")
                        .setDoNotTrack(true) // With "do not track" set, the user is opted out!
                        .build();

                MobileFuse.setPrivacyPreferences(privacyPrefs);



            }
        });
    }

    public boolean isInitialized() {
        return VungleAds.isInitialized();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean haveExtraSettings() {
        return this.m_minimumSpaceForInit >= 0 || this.m_minimumSpaceForAd >= 0 || this.m_disableHardwareId != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /*
     * public VungleSettings GetInitializationSettings() {
     * VungleSettings.Builder builder = new VungleSettings.Builder();
     * if (this.m_minimumSpaceForInit >= 0) {
     * builder.setMinimumSpaceForInit(this.m_minimumSpaceForInit);
     * }
     * if (this.m_minimumSpaceForAd >= 0) {
     * builder.setMinimumSpaceForAd(this.m_minimumSpaceForAd);
     * }
     * if (this.m_disableHardwareId != null) {
     * builder.setAndroidIdOptOut(this.m_disableHardwareId.booleanValue());
     * }
     * VungleSettings settings = builder.build();
     * return settings;
     * }
     * 
     *//* JADX INFO: Access modifiers changed from: private *//*
                                                               * public VungleSettings GetDefaultEmptySettings() {
                                                               * 
                                                               * VungleSettings.Builder builder = new
                                                               * VungleSettings.Builder();
                                                               * VungleSettings settings = builder.build();
                                                               * return settings;
                                                               * }
                                                               */

    public void setMinimumDiskSpaceForInit(long minimumSpace) {
        Log.i(LOG_TAG, "SET MINIMUM SPACE FOR INIT ");
        this.m_minimumSpaceForInit = minimumSpace;
    }

    public void setMinimumDiskSpaceForAd(long minimumSpace) {
        Log.i(LOG_TAG, "SET MINIMUM SPACE FOR AD ");
        this.m_minimumSpaceForAd = minimumSpace;
    }

    public void setHardwareIdOptOut(boolean disableHardwareID) {
        Log.i(LOG_TAG, "SET DISABLE HARDWARE ID ");
        this.m_disableHardwareId = new Boolean(disableHardwareID);
    }

    public void onPause() {
        for (Map.Entry<String, IVungleAd> adEntry : vungleAds.entrySet()) {
            IVungleAd ad = adEntry.getValue();
            if (ad instanceof IVungleFeed) {
                ((IVungleFeed) ad).pause();
            }
        }
    }

    public void onResume() {
        for (Map.Entry<String, IVungleAd> adEntry : vungleAds.entrySet()) {
            IVungleAd ad = adEntry.getValue();
            if (ad instanceof IVungleFeed) {
                ((IVungleFeed) ad).resume();
            }
        }
    }

    public void setSoundEnabled(boolean isEnabled) {
        isSoundEnabled = isEnabled;
    }

    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }

    public boolean closeAd(String placementId) {
        return false;
    }

    public void loadBanner(String placementId, int adSizeAsInt, int gravityAsInt) {
        // if (vungleAds.containsKey(placementId)) {
        // IVungleAd oldAd = vungleAds.get(placementId);
        // oldAd.close();
        // }
        /*
         * TopLeft = 0,
         * TopCenter=1,
         * TopRight = 2,
         * Centered = 3,
         * BottomLeft = 4,
         * BottomCenter = 5,
         * BottomRight = 6,
         */
        if (gravityAsInt == 0) {
            bannerGravityAsInt = Gravity.FILL_VERTICAL;
        } else if (gravityAsInt == 1) {
            bannerGravityAsInt = Gravity.CENTER_HORIZONTAL;
        } else if (gravityAsInt == 2) {
            bannerGravityAsInt = Gravity.TOP;
        } else if (gravityAsInt == 3) {
            bannerGravityAsInt = Gravity.CENTER;
        } else if (gravityAsInt == 4) {
            bannerGravityAsInt = Gravity.RIGHT | Gravity.BOTTOM;
        } else if (gravityAsInt == 5) {
            bannerGravityAsInt = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        } else if (gravityAsInt == 6) {
            bannerGravityAsInt = Gravity.RIGHT | Gravity.BOTTOM;
        }
        bannerAd = new BannerAd(getActivity(), placementId, BannerAdSize.BANNER);
        bannerAd.load("");
        bannerAd.setAdListener(this);

        // VungleBanner ad = new VungleBanner(getActivity(), placementId, adSizeAsInt,
        // gravityAsInt);
        // vungleAds.put(placementId, ad);
        // ad.load();
    }

    public void showBanner(String placementId) {
        // IVungleAd ad;
        // if (!vungleAds.containsKey(placementId) || (ad = vungleAds.get(placementId))
        // == null) {
        // return;
        // }
        if (bannerAd != null && bannerAd.canPlayAd()) {
            BannerView bannerView = bannerAd.getBannerView();
            // bannerView.setBackground(Drawable.);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    bannerGravityAsInt);
            getActivity().addContentView(bannerView, params);
            // binding.adContainer.addView(bannerView, params);

        }
        // ad.show();
    }

    public void showBanner(String placementId, String options) {
        IVungleAd ad;
        if (!vungleAds.containsKey(placementId) || (ad = vungleAds.get(placementId)) == null) {
            return;
        }

        ad.show(options);
    }

    public void closeBanner(String placementId) {
        // if(bannerAd != null){

        // bannerAd.finishAd();
        // bannerAd = null;
        // }

        Utility.runSafelyOnUiThread(getActivity(), new Runnable() { // from class:
                                                                    // com.vungle.androidplugin.VunglePlugin.1
            @Override // java.lang.Runnable
            public void run() {
                if (bannerAd != null) {

                    bannerAd.finishAd();
                    bannerAd = null;
                }

            }
        });
        //
        // IVungleAd ad;
        // if (!vungleAds.containsKey(placementId) || (ad = vungleAds.get(placementId))
        // == null) {
        // return;
        // }
        // ad.close();
        // vungleAds.remove(placementId);
    }

    public void setOffset(String placementId, int x, int y) {
        if (!vungleAds.containsKey(placementId)) {
            return;
        }
        IVungleAd ad = vungleAds.get(placementId);
        if (!(ad instanceof IVungleFeed)) {
            return;
        }
        IVungleFeed feedAd = (IVungleFeed) ad;
        feedAd.setOffset(x, y);
    }

    public String getSdkVersion() {
        return "7.1.0";
    }

    public void setLogEnabled(boolean isEnabled) {
        Utility.setLogEnabled(isEnabled);
    }

    @Override
    public void onAdLoaded(@NonNull BaseAd baseAd) {
        // 这里处理回调逻辑
        showBanner("");
    }

    @Override
    public void onAdStart(@NonNull BaseAd baseAd) {

    }

    @Override
    public void onAdImpression(@NonNull BaseAd baseAd) {

    }

    @Override
    public void onAdEnd(@NonNull BaseAd baseAd) {

    }

    @Override
    public void onAdClicked(@NonNull BaseAd baseAd) {

    }

    @Override
    public void onAdLeftApplication(@NonNull BaseAd baseAd) {

    }

    @Override
    public void onAdFailedToLoad(@NonNull BaseAd baseAd, @NonNull VungleError adError) {

    }

    @Override
    public void onAdFailedToPlay(@NonNull BaseAd baseAd, @NonNull VungleError adError) {

    }
}