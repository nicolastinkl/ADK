package com.iab.omid.library.bytedance2.publisher;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.webkit.WebView;
import com.iab.omid.library.bytedance2.adsession.AdSessionContext;
import com.iab.omid.library.bytedance2.adsession.C1195a;
import com.iab.omid.library.bytedance2.adsession.VerificationScriptResource;
import com.iab.omid.library.bytedance2.internal.C1209f;
import com.iab.omid.library.bytedance2.internal.C1210g;
import com.iab.omid.library.bytedance2.utils.C1224c;
import com.iab.omid.library.bytedance2.utils.C1230f;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.publisher.b */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/publisher/b.class */
public class C1220b extends AdSessionStatePublisher {

    /* renamed from: f */
    private WebView f6769f;

    /* renamed from: g */
    private Long f6770g = null;

    /* renamed from: h */
    private final Map<String, VerificationScriptResource> f6771h;

    /* renamed from: i */
    private final String f6772i;

    /* renamed from: com.iab.omid.library.bytedance2.publisher.b$a */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/publisher/b$a.class */
    class RunnableC1221a implements Runnable {

        /* renamed from: a */
        private final WebView f6773a;

        RunnableC1221a() {
            this.f6773a = C1220b.this.f6769f;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.f6773a.destroy();
        }
    }

    public C1220b(Map<String, VerificationScriptResource> map, String str) {
        this.f6771h = map;
        this.f6772i = str;
    }

    @Override // com.iab.omid.library.bytedance2.publisher.AdSessionStatePublisher
    /* renamed from: i */
    public void mo107i() {
        super.mo107i();
        m106j();
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    /* renamed from: j */
    void m106j() {
        WebView webView = new WebView(C1209f.m165b().m167a());
        this.f6769f = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        this.f6769f.getSettings().setAllowContentAccess(false);
        this.f6769f.getSettings().setAllowFileAccess(false);
        m129a(this.f6769f);
        C1210g.m164a().m148c(this.f6769f, this.f6772i);
        for (String str : this.f6771h.keySet()) {
            C1210g.m164a().m159a(this.f6769f, this.f6771h.get(str).getResourceUrl().toExternalForm(), str);
        }
        this.f6770g = Long.valueOf(C1230f.m72b());
    }

    @Override // com.iab.omid.library.bytedance2.publisher.AdSessionStatePublisher
    /* renamed from: a */
    public void mo110a(C1195a c1195a, AdSessionContext adSessionContext) {
        JSONObject jSONObject = new JSONObject();
        Map<String, VerificationScriptResource> injectedResourcesMap = adSessionContext.getInjectedResourcesMap();
        for (String str : injectedResourcesMap.keySet()) {
            C1224c.m89a(jSONObject, str, injectedResourcesMap.get(str).toJsonObject());
        }
        m125a(c1195a, adSessionContext, jSONObject);
    }

    @Override // com.iab.omid.library.bytedance2.publisher.AdSessionStatePublisher
    /* renamed from: b */
    public void mo108b() {
        super.mo108b();
        new Handler().postDelayed(new RunnableC1221a(), Math.max(4000 - (this.f6770g == null ? 4000L : TimeUnit.MILLISECONDS.convert(C1230f.m72b() - this.f6770g.longValue(), TimeUnit.NANOSECONDS)), 2000L));
        this.f6769f = null;
    }
}
