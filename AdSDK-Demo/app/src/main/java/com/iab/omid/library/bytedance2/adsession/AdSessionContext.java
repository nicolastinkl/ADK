package com.iab.omid.library.bytedance2.adsession;

import android.webkit.WebView;
import androidx.annotation.Nullable;
import com.iab.omid.library.bytedance2.utils.C1231g;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/AdSessionContext.class */
public final class AdSessionContext {
    private final Partner partner;
    private final WebView webView;
    private final List<VerificationScriptResource> verificationScriptResources;
    private final Map<String, VerificationScriptResource> injectedResourcesMap;
    private final String omidJsScriptContent;
    private final String customReferenceData;
    @Nullable
    private final String contentUrl;
    private final AdSessionContextType adSessionContextType;

    private AdSessionContext(Partner partner, WebView webView, String str, List<VerificationScriptResource> list, @Nullable String str2, String str3, AdSessionContextType adSessionContextType) {
        ArrayList arrayList = new ArrayList();
        this.verificationScriptResources = arrayList;
        this.injectedResourcesMap = new HashMap();
        this.partner = partner;
        this.webView = webView;
        this.omidJsScriptContent = str;
        this.adSessionContextType = adSessionContextType;
        if (list != null) {
            arrayList.addAll(list);
            for (VerificationScriptResource verificationScriptResource : list) {
                this.injectedResourcesMap.put(UUID.randomUUID().toString(), verificationScriptResource);
            }
        }
        this.contentUrl = str2;
        this.customReferenceData = str3;
    }

    public static AdSessionContext createHtmlAdSessionContext(Partner partner, WebView webView, @Nullable String str, String str2) {
        C1231g.m68a(partner, "Partner is null");
        C1231g.m68a(webView, "WebView is null");
        if (str2 != null) {
            C1231g.m67a(str2, 256, "CustomReferenceData is greater than 256 characters");
        }
        return new AdSessionContext(partner, webView, null, null, str, str2, AdSessionContextType.HTML);
    }

    public static AdSessionContext createJavascriptAdSessionContext(Partner partner, WebView webView, @Nullable String str, String str2) {
        C1231g.m68a(partner, "Partner is null");
        C1231g.m68a(webView, "WebView is null");
        if (str2 != null) {
            C1231g.m67a(str2, 256, "CustomReferenceData is greater than 256 characters");
        }
        return new AdSessionContext(partner, webView, null, null, str, str2, AdSessionContextType.JAVASCRIPT);
    }

    public static AdSessionContext createNativeAdSessionContext(Partner partner, String str, List<VerificationScriptResource> list, @Nullable String str2, String str3) {
        C1231g.m68a(partner, "Partner is null");
        C1231g.m68a((Object) str, "OM SDK JS script content is null");
        C1231g.m68a(list, "VerificationScriptResources is null");
        if (str3 != null) {
            C1231g.m67a(str3, 256, "CustomReferenceData is greater than 256 characters");
        }
        return new AdSessionContext(partner, null, str, list, str2, str3, AdSessionContextType.NATIVE);
    }

    public Partner getPartner() {
        return this.partner;
    }

    public List<VerificationScriptResource> getVerificationScriptResources() {
        return Collections.unmodifiableList(this.verificationScriptResources);
    }

    public Map<String, VerificationScriptResource> getInjectedResourcesMap() {
        return Collections.unmodifiableMap(this.injectedResourcesMap);
    }

    public WebView getWebView() {
        return this.webView;
    }

    @Nullable
    public String getContentUrl() {
        return this.contentUrl;
    }

    public String getCustomReferenceData() {
        return this.customReferenceData;
    }

    public String getOmidJsScriptContent() {
        return this.omidJsScriptContent;
    }

    public AdSessionContextType getAdSessionContextType() {
        return this.adSessionContextType;
    }
}
