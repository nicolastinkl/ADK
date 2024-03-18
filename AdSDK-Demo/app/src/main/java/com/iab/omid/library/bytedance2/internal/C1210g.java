package com.iab.omid.library.bytedance2.internal;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import com.iab.omid.library.bytedance2.adsession.ErrorType;
import com.iab.omid.library.bytedance2.utils.C1227d;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.internal.g */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/internal/g.class */
public class C1210g {

    /* renamed from: a */
    private static C1210g f6746a = new C1210g();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.iab.omid.library.bytedance2.internal.g$a */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/internal/g$a.class */
    public class RunnableC1211a implements Runnable {

        /* renamed from: a */
        final /* synthetic */ WebView f6747a;

        /* renamed from: b */
        final /* synthetic */ String f6748b;

        RunnableC1211a(WebView webView, String str) {
            this.f6747a = webView;
            this.f6748b = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            C1210g.this.m148c(this.f6747a, this.f6748b);
        }
    }

    private C1210g() {
    }

    /* renamed from: a */
    public static final C1210g m164a() {
        return f6746a;
    }

    /* renamed from: c */
    public boolean m148c(WebView webView, String str) {
        if (webView == null || TextUtils.isEmpty(str)) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 19) {
            webView.loadUrl("javascript: " + str);
            return true;
        }
        try {
            webView.evaluateJavascript(str, null);
            return true;
        } catch (IllegalStateException unused) {
            webView.loadUrl("javascript: " + str);
            return true;
        }
    }

    /* renamed from: a */
    public void m159a(WebView webView, String str, String str2) {
        if (str == null || TextUtils.isEmpty(str2)) {
            return;
        }
        m148c(webView, "(function() {this.omidVerificationProperties = this.omidVerificationProperties || {};Object.defineProperty(this.omidVerificationProperties, 'injectionId', {get: function() {var currentScript = document && document.currentScript;return currentScript && currentScript.getAttribute('data-injection-id');}, configurable: true});var script = document.createElement('script');script.setAttribute(\"type\",\"text/javascript\");script.setAttribute(\"src\",\"%SCRIPT_SRC%\");script.setAttribute(\"data-injection-id\",\"%INJECTION_ID%\");document.body.appendChild(script);})();".replace("%SCRIPT_SRC%", str).replace("%INJECTION_ID%", str2));
    }

    /* renamed from: a */
    public void m154a(WebView webView, JSONObject jSONObject) {
        m156a(webView, "init", jSONObject);
    }

    /* renamed from: a */
    public void m157a(WebView webView, String str, JSONObject jSONObject, JSONObject jSONObject2, JSONObject jSONObject3) {
        m156a(webView, "startSession", str, jSONObject, jSONObject2, jSONObject3);
    }

    /* renamed from: a */
    public void m163a(WebView webView) {
        m156a(webView, "finishSession", new Object[0]);
    }

    /* renamed from: a */
    public void m160a(WebView webView, String str) {
        m156a(webView, "setNativeViewHierarchy", str);
    }

    /* renamed from: b */
    public void m151b(WebView webView, String str) {
        m156a(webView, "setState", str);
    }

    /* renamed from: b */
    public void m152b(WebView webView) {
        m156a(webView, "publishImpressionEvent", new Object[0]);
    }

    /* renamed from: c */
    public void m149c(WebView webView) {
        m156a(webView, "publishLoadedEvent", new Object[0]);
    }

    /* renamed from: b */
    public void m150b(WebView webView, @NonNull JSONObject jSONObject) {
        m156a(webView, "publishLoadedEvent", jSONObject);
    }

    /* renamed from: a */
    public void m161a(WebView webView, ErrorType errorType, String str) {
        m156a(webView, "error", errorType.toString(), str);
    }

    /* renamed from: a */
    public void m158a(WebView webView, String str, JSONObject jSONObject) {
        if (jSONObject != null) {
            m156a(webView, "publishMediaEvent", str, jSONObject);
        } else {
            m156a(webView, "publishMediaEvent", str);
        }
    }

    /* renamed from: a */
    public void m162a(WebView webView, float f) {
        m156a(webView, "setDeviceVolume", Float.valueOf(f));
    }

    /* renamed from: c */
    public void m147c(WebView webView, @NonNull JSONObject jSONObject) {
        m156a(webView, "setLastActivity", jSONObject);
    }

    @VisibleForTesting
    /* renamed from: a */
    void m156a(WebView webView, String str, Object... objArr) {
        if (webView == null) {
            C1227d.m78a("The WebView is null for " + str);
            return;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("if(window.omidBridge!==undefined){omidBridge.");
        sb.append(str);
        sb.append("(");
        m153a(sb, objArr);
        sb.append(")}");
        m155a(webView, sb);
    }

    @VisibleForTesting
    /* renamed from: a */
    void m155a(WebView webView, StringBuilder sb) {
        String sb2 = sb.toString();
        Handler handler = webView.getHandler();
        if (handler == null || Looper.myLooper() == handler.getLooper()) {
            m148c(webView, sb2);
        } else {
            handler.post(new RunnableC1211a(webView, sb2));
        }
    }

    @VisibleForTesting
    /* renamed from: a */
    void m153a(StringBuilder sb, Object[] objArr) {
        if (objArr == null || objArr.length <= 0) {
            return;
        }
        for (Object obj : objArr) {
            if (obj == null) {
                sb.append('\"').append('\"');
            } else if (obj instanceof String) {
                String obj2 = obj.toString();
                if (obj2.startsWith("{")) {
                    sb.append(obj2);
                } else {
                    sb.append('\"').append(obj2).append('\"');
                }
            } else {
                sb.append(obj);
            }
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
    }
}
