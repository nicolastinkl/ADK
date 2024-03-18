//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.publisher;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.iab.omid.library.vungle.adsession.AdSessionContext;
import com.iab.omid.library.vungle.adsession.VerificationScriptResource;
import com.iab.omid.library.vungle.adsession.a;
import com.iab.omid.library.vungle.internal.f;
import com.iab.omid.library.vungle.internal.g;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class b extends AdSessionStatePublisher {
    private WebView f;
    private Long g = null;
    private final Map<String, VerificationScriptResource> h;
    private final String i;

    public b(Map<String, VerificationScriptResource> var1, String var2) {
        this.h = var1;
        this.i = var2;
    }

    public void i() {
        super.i();
        this.j();
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    void j() {
        WebView var1;
        WebView var10007 = var1 = new WebView(com.iab.omid.library.vungle.internal.f.b().a());
        this.f = var10007;
        var1.getSettings().setJavaScriptEnabled(true);
        this.f.getSettings().setAllowContentAccess(false);
        this.f.getSettings().setAllowFileAccess(false);
        this.f.setWebViewClient(new WebViewClient() {
            String a = "OMID NativeBridge WebViewClient";

            public boolean onRenderProcessGone(WebView var1, RenderProcessGoneDetail var2) {
                Log.w(this.a, "WebView renderer gone: " + var2.toString());
                if (b.this.getWebView() == var1) {
                    Log.w(this.a, "Deallocating the Native bridge as it is unusable. No further events will be generated for this session.");
                    b.this.a((WebView)null);
                    var1.destroy();
                    return true;
                } else {
                    return super.onRenderProcessGone(var1, var2);
                }
            }
        });
        this.a(this.f);
        g var10001 = com.iab.omid.library.vungle.internal.g.a();
        var1 = this.f;
        var10001.c(var1, this.i);
        Iterator var4 = this.h.keySet().iterator();

        while(var4.hasNext()) {
            String var2 = (String)var4.next();
            String var3 = ((VerificationScriptResource)this.h.get(var2)).getResourceUrl().toExternalForm();
            com.iab.omid.library.vungle.internal.g.a().a(this.f, var3, var2);
        }

        this.g = com.iab.omid.library.vungle.utils.f.b();
    }

    public void a(a param1, AdSessionContext param2) {
        // $FF: Couldn't be decompiled
    }

    public void b() {
        super.b();
        long var1 = 2000L;
        long var3 = 4000L;
        long var5;
        if (this.g == null) {
            var5 = var3;
        } else {
            var5 = TimeUnit.MILLISECONDS.convert(com.iab.omid.library.vungle.utils.f.b() - this.g, TimeUnit.NANOSECONDS);
        }

        var1 = Math.max(var3 - var5, var1);
        (new Handler()).postDelayed(new Runnable() {
            private final WebView a;

            {
                this.a = b.this.f;
            }

            public void run() {
                this.a.destroy();
            }
        }, var1);
        this.f = null;
    }
}
