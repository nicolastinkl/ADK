//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.publisher;

import android.webkit.WebView;
import androidx.annotation.NonNull;
import com.iab.omid.library.vungle.adsession.AdEvents;
import com.iab.omid.library.vungle.adsession.AdSessionConfiguration;
import com.iab.omid.library.vungle.adsession.AdSessionContext;
import com.iab.omid.library.vungle.adsession.ErrorType;
import com.iab.omid.library.vungle.adsession.VerificationScriptResource;
import com.iab.omid.library.vungle.adsession.media.MediaEvents;
import com.iab.omid.library.vungle.internal.f;
import com.iab.omid.library.vungle.internal.g;
import com.iab.omid.library.vungle.utils.c;
import com.iab.omid.library.vungle.weakreference.b;
import java.util.Date;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AdSessionStatePublisher {
    private b a1;
    private AdEvents b;
    private MediaEvents c;
    private a d;
    private long e;

    public static enum a {
        a,
        b,
        c;

        private a() {
        }

    }

    public AdSessionStatePublisher() {
        AdSessionStatePublisher var10000 = this;

        this.a();
        b var1;
        var1 = new b((WebView)null);
        var10000.a1 = var1;
    }

    public void i() {
    }

    public void b() {
        this.a1.clear();
    }

    void a(WebView var1) {
        AdSessionStatePublisher var10000 = this;
        b var2;
        var2 = new b(var1);
        var10000.a1 = var2;
    }

    public WebView getWebView() {
        return (WebView)this.a1.get();
    }

    public AdEvents c() {
        return this.b;
    }

    public void a(AdEvents var1) {
        this.b = var1;
    }

    public MediaEvents d() {
        return this.c;
    }

    public void a(MediaEvents var1) {
        this.c = var1;
    }

    public boolean e() {
        return this.a1.get() != null;
    }

    public void a(boolean var1) {
        if (this.e()) {
            String var2;
            if (var1) {
                var2 = "foregrounded";
            } else {
                var2 = "backgrounded";
            }

            g.a().b(this.getWebView(), var2);
        }

    }

    public void b(String var1, long var2) {
        if (var2 >= this.e) {
            this.d = AdSessionStatePublisher.a.b;
            g.a().a(this.getWebView(), var1);
        }

    }

    public void a(String var1, long var2) {
        a var4;
        if (var2 >= this.e && this.d != (var4 = AdSessionStatePublisher.a.c)) {
            this.d = var4;
            g.a().a(this.getWebView(), var1);
        }

    }

    public void a(AdSessionConfiguration var1) {
        g var10000 = g.a();
        WebView var2 = this.getWebView();
        var10000.a(var2, var1.toJsonObject());
    }

    public void a(com.iab.omid.library.vungle.adsession.a var1, AdSessionContext var2) {
        this.a(var1, var2, (JSONObject)null);
    }

    protected void a(com.iab.omid.library.vungle.adsession.a var1, AdSessionContext var2, JSONObject var3) {
        String var7 = var1.getAdSessionId();
        JSONObject var4;
        JSONObject var10001 = var4 = new JSONObject();
        com.iab.omid.library.vungle.utils.c.a(var4, "environment", "app");
        com.iab.omid.library.vungle.utils.c.a(var10001, "adSessionType", var2.getAdSessionContextType());
        com.iab.omid.library.vungle.utils.c.a(var10001, "deviceInfo", com.iab.omid.library.vungle.utils.b.d());
        com.iab.omid.library.vungle.utils.c.a(var10001, "deviceCategory", com.iab.omid.library.vungle.utils.a.a().toString());
        JSONArray var5;
        JSONArray var10004 = var5 = new JSONArray();
        var10004.put("clid");
        var10004.put("vlid");
        com.iab.omid.library.vungle.utils.c.a(var10001, "supports", var5);
        JSONObject var9;
        JSONObject var10003 = var9 = new JSONObject();
        com.iab.omid.library.vungle.utils.c.a(var9, "partnerName", var2.getPartner().getName());
        com.iab.omid.library.vungle.utils.c.a(var10003, "partnerVersion", var2.getPartner().getVersion());
        com.iab.omid.library.vungle.utils.c.a(var10001, "omidNativeInfo", var9);
        JSONObject var10002 = var9 = new JSONObject();
        com.iab.omid.library.vungle.utils.c.a(var10002, "libraryVersion", "1.4.8-Vungle");
        com.iab.omid.library.vungle.utils.c.a(var10002, "appId", f.b().a().getApplicationContext().getPackageName());
        com.iab.omid.library.vungle.utils.c.a(var10001, "app", var9);
        if (var2.getContentUrl() != null) {
            com.iab.omid.library.vungle.utils.c.a(var4, "contentUrl", var2.getContentUrl());
        }

        if (var2.getCustomReferenceData() != null) {
            com.iab.omid.library.vungle.utils.c.a(var4, "customReferenceData", var2.getCustomReferenceData());
        }

        AdSessionContext var10000 = var2;
        JSONObject var8;
        var8 = new JSONObject();
        Iterator var10 = var10000.getVerificationScriptResources().iterator();

        while(var10.hasNext()) {
            VerificationScriptResource var11 = (VerificationScriptResource)var10.next();
            String var6 = var11.getVendorKey();
            com.iab.omid.library.vungle.utils.c.a(var8, var6, var11.getVerificationParameters());
        }

        g.a().a(this.getWebView(), var7, var4, var8, var3);
    }

    public void f() {
        g.a().a(this.getWebView());
    }

    public void a(ErrorType var1, String var2) {
        g.a().a(this.getWebView(), var1, var2);
    }

    public void g() {
        g.a().b(this.getWebView());
    }

    public void h() {
        g.a().c(this.getWebView());
    }

    public void a(@NonNull JSONObject var1) {
        g.a().b(this.getWebView(), var1);
    }

    public void a(String var1) {
        g.a().a(this.getWebView(), var1, (JSONObject)null);
    }

    public void a(String var1, JSONObject var2) {
        g.a().a(this.getWebView(), var1, var2);
    }

    public void a(@NonNull Date var1) {
        if (var1 != null) {
            JSONObject var2;
            JSONObject var10000 = var2 = new JSONObject();
            com.iab.omid.library.vungle.utils.c.a(var10000, "timestamp", var1.getTime());
            g.a().c(this.getWebView(), var2);
        }
    }

    public void a(float var1) {
        g.a().a(this.getWebView(), var1);
    }

    public void a() {
        this.e = com.iab.omid.library.vungle.utils.f.b();
        this.d = AdSessionStatePublisher.a.a;

    }


}
