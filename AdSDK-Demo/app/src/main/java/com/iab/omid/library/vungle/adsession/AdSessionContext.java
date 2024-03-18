//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.adsession;

import android.webkit.WebView;
import androidx.annotation.Nullable;
import com.iab.omid.library.vungle.utils.g;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AdSessionContext {
    private  Partner partner;
    private  WebView webView;
    private  List<VerificationScriptResource> verificationScriptResources;
    private  Map<String, VerificationScriptResource> injectedResourcesMap;
    private  String omidJsScriptContent;
    private  String customReferenceData;
    @Nullable
    private  String contentUrl;
    private  AdSessionContextType adSessionContextType;

    public AdSessionContext(Partner var1, WebView var2, String var3, List<VerificationScriptResource> var4, @Nullable String var5, String var6, AdSessionContextType var7) {
        ArrayList var8;
        var8 =new ArrayList<>();
        this.verificationScriptResources = var8;
        HashMap var9;
        var9 = new HashMap<>();
        this.injectedResourcesMap = var9;
        this.partner = var1;
        this.webView = var2;
        this.omidJsScriptContent = var3;
        this.adSessionContextType = var7;
        if (var4 != null) {
            var8.addAll(var4);
            Iterator var10 = var4.iterator();

            while(var10.hasNext()) {
                VerificationScriptResource var11 = (VerificationScriptResource)var10.next();
                var3 = UUID.randomUUID().toString();
                this.injectedResourcesMap.put(var3, var11);
            }
        }

        this.contentUrl = var5;
        this.customReferenceData = var6;
    }

    public AdSessionContext() {

    }

    public static AdSessionContext createHtmlAdSessionContext(Partner var0, WebView var1, @Nullable String var2, String var3) {
        g.a(var0, "Partner is null");
        g.a(var1, "WebView is null");
        if (var3 != null) {
            g.a(var3, 256, "CustomReferenceData is greater than 256 characters");
        }

        AdSessionContextType var4 = AdSessionContextType.HTML;
        return new AdSessionContext(var0, var1, (String)null, (List)null, var2, var3, var4);
    }

    public static AdSessionContext createJavascriptAdSessionContext(Partner var0, WebView var1, @Nullable String var2, String var3) {
        g.a(var0, "Partner is null");
        g.a(var1, "WebView is null");
        if (var3 != null) {
            g.a(var3, 256, "CustomReferenceData is greater than 256 characters");
        }

        AdSessionContextType var4 = AdSessionContextType.JAVASCRIPT;
        return new AdSessionContext(var0, var1, (String)null, (List)null, var2, var3, var4);
    }

    public static AdSessionContext createNativeAdSessionContext(Partner var0, String var1, List<VerificationScriptResource> var2, @Nullable String var3, String var4) {
        g.a(var0, "Partner is null");
        g.a(var1, "OM SDK JS script content is null");
        g.a(var2, "VerificationScriptResources is null");
        if (var4 != null) {
            g.a(var4, 256, "CustomReferenceData is greater than 256 characters");
        }

        AdSessionContextType var5 = AdSessionContextType.NATIVE;
        return new AdSessionContext(var0, (WebView)null, var1, var2, var3, var4, var5);
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
