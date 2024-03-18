package com.iab.omid.library.bytedance2.publisher;

import android.webkit.WebView;
import androidx.annotation.NonNull;
import com.iab.omid.library.bytedance2.adsession.AdEvents;
import com.iab.omid.library.bytedance2.adsession.AdSessionConfiguration;
import com.iab.omid.library.bytedance2.adsession.AdSessionContext;
import com.iab.omid.library.bytedance2.adsession.C1195a;
import com.iab.omid.library.bytedance2.adsession.ErrorType;
import com.iab.omid.library.bytedance2.adsession.VerificationScriptResource;
import com.iab.omid.library.bytedance2.adsession.media.MediaEvents;
import com.iab.omid.library.bytedance2.internal.C1209f;
import com.iab.omid.library.bytedance2.internal.C1210g;
import com.iab.omid.library.bytedance2.utils.C1222a;
import com.iab.omid.library.bytedance2.utils.C1223b;
import com.iab.omid.library.bytedance2.utils.C1224c;
import com.iab.omid.library.bytedance2.utils.C1230f;
import com.iab.omid.library.bytedance2.weakreference.C1249b;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/publisher/AdSessionStatePublisher.class */
public abstract class AdSessionStatePublisher {

    /* renamed from: a */
    private C1249b f6760a;

    /* renamed from: b */
    private AdEvents f6761b;

    /* renamed from: c */
    private MediaEvents f6762c;

    /* renamed from: d */
    private EnumC1218a f6763d;

    /* renamed from: e */
    private long f6764e;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.iab.omid.library.bytedance2.publisher.AdSessionStatePublisher$a */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/publisher/AdSessionStatePublisher$a.class */
    public enum EnumC1218a {
        AD_STATE_IDLE,
        AD_STATE_VISIBLE,
        AD_STATE_NOTVISIBLE
    }

    public AdSessionStatePublisher() {
        m131a();
        this.f6760a = new C1249b(null);
    }

    /* renamed from: i */
    public void mo107i() {
    }

    /* renamed from: b */
    public void mo108b() {
        this.f6760a.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m129a(WebView webView) {
        this.f6760a = new C1249b(webView);
    }

    public WebView getWebView() {
        return this.f6760a.get();
    }

    /* renamed from: c */
    public AdEvents m116c() {
        return this.f6761b;
    }

    /* renamed from: a */
    public void m128a(AdEvents adEvents) {
        this.f6761b = adEvents;
    }

    /* renamed from: d */
    public MediaEvents m115d() {
        return this.f6762c;
    }

    /* renamed from: a */
    public void m124a(MediaEvents mediaEvents) {
        this.f6762c = mediaEvents;
    }

    /* renamed from: e */
    public boolean m114e() {
        return this.f6760a.get() != null;
    }

    /* renamed from: a */
    public void m118a(boolean z) {
        if (m114e()) {
            C1210g.m164a().m151b(getWebView(), z ? "foregrounded" : "backgrounded");
        }
    }

    /* renamed from: b */
    public void m117b(String str, long j) {
        if (j >= this.f6764e) {
            this.f6763d = EnumC1218a.AD_STATE_VISIBLE;
            C1210g.m164a().m160a(getWebView(), str);
        }
    }

    /* renamed from: a */
    public void m122a(String str, long j) {
        if (j >= this.f6764e) {
            EnumC1218a enumC1218a = this.f6763d;
            EnumC1218a enumC1218a2 = EnumC1218a.AD_STATE_NOTVISIBLE;
            if (enumC1218a != enumC1218a2) {
                this.f6763d = enumC1218a2;
                C1210g.m164a().m160a(getWebView(), str);
            }
        }
    }

    /* renamed from: a */
    public void m127a(AdSessionConfiguration adSessionConfiguration) {
        C1210g.m164a().m154a(getWebView(), adSessionConfiguration.toJsonObject());
    }

    /* renamed from: a */
    public void mo110a(C1195a c1195a, AdSessionContext adSessionContext) {
        m125a(c1195a, adSessionContext, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: a */
    public void m125a(C1195a c1195a, AdSessionContext adSessionContext, JSONObject jSONObject) {
        String adSessionId = c1195a.getAdSessionId();
        JSONObject jSONObject2 = new JSONObject();
        C1224c.m89a(jSONObject2, "environment", "app");
        C1224c.m89a(jSONObject2, "adSessionType", adSessionContext.getAdSessionContextType());
        C1224c.m89a(jSONObject2, "deviceInfo", C1223b.m100d());
        C1224c.m89a(jSONObject2, "deviceCategory", C1222a.m105a().toString());
        JSONArray jSONArray = new JSONArray();
        jSONArray.put("clid");
        jSONArray.put("vlid");
        C1224c.m89a(jSONObject2, "supports", jSONArray);
        JSONObject jSONObject3 = new JSONObject();
        C1224c.m89a(jSONObject3, "partnerName", adSessionContext.getPartner().getName());
        C1224c.m89a(jSONObject3, "partnerVersion", adSessionContext.getPartner().getVersion());
        C1224c.m89a(jSONObject2, "omidNativeInfo", jSONObject3);
        JSONObject jSONObject4 = new JSONObject();
        C1224c.m89a(jSONObject4, "libraryVersion", "1.4.4-Bytedance2");
        C1224c.m89a(jSONObject4, "appId", C1209f.m165b().m167a().getApplicationContext().getPackageName());
        C1224c.m89a(jSONObject2, "app", jSONObject4);
        if (adSessionContext.getContentUrl() != null) {
            C1224c.m89a(jSONObject2, "contentUrl", adSessionContext.getContentUrl());
        }
        if (adSessionContext.getCustomReferenceData() != null) {
            C1224c.m89a(jSONObject2, "customReferenceData", adSessionContext.getCustomReferenceData());
        }
        JSONObject jSONObject5 = new JSONObject();
        for (VerificationScriptResource verificationScriptResource : adSessionContext.getVerificationScriptResources()) {
            C1224c.m89a(jSONObject5, verificationScriptResource.getVendorKey(), verificationScriptResource.getVerificationParameters());
        }
        C1210g.m164a().m157a(getWebView(), adSessionId, jSONObject2, jSONObject5, jSONObject);
    }

    /* renamed from: f */
    public void m113f() {
        C1210g.m164a().m163a(getWebView());
    }

    /* renamed from: a */
    public void m126a(ErrorType errorType, String str) {
        C1210g.m164a().m161a(getWebView(), errorType, str);
    }

    /* renamed from: g */
    public void m112g() {
        C1210g.m164a().m152b(getWebView());
    }

    /* renamed from: h */
    public void m111h() {
        C1210g.m164a().m149c(getWebView());
    }

    /* renamed from: a */
    public void m119a(@NonNull JSONObject jSONObject) {
        C1210g.m164a().m150b(getWebView(), jSONObject);
    }

    /* renamed from: a */
    public void m123a(String str) {
        C1210g.m164a().m158a(getWebView(), str, (JSONObject) null);
    }

    /* renamed from: a */
    public void m121a(String str, JSONObject jSONObject) {
        C1210g.m164a().m158a(getWebView(), str, jSONObject);
    }

    /* renamed from: a */
    public void m120a(@NonNull Date date) {
        if (date == null) {
            return;
        }
        JSONObject jSONObject = new JSONObject();
        C1224c.m89a(jSONObject, "timestamp", Long.valueOf(date.getTime()));
        C1210g.m164a().m147c(getWebView(), jSONObject);
    }

    /* renamed from: a */
    public void m130a(float f) {
        C1210g.m164a().m162a(getWebView(), f);
    }

    /* renamed from: a */
    public void m131a() {
        this.f6764e = C1230f.m72b();
        this.f6763d = EnumC1218a.AD_STATE_IDLE;
    }
}
