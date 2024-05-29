//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import com.iab.omid.library.vungle.adsession.ErrorType;
import com.iab.omid.library.vungle.utils.d;
import org.json.JSONObject;

public class g {
    private static g a = new g();

    private g() {
    }

    public static final g a() {
        return a;
    }

    public boolean c(WebView var1, String var2) {
        if (var1 != null && !TextUtils.isEmpty(var2)) {
            if (VERSION.SDK_INT >= 19) {
                try {
                    var1.evaluateJavascript(var2, (ValueCallback)null);
                } catch (IllegalStateException var3) {
                    var1.loadUrl("javascript: " + var2);
                }
            } else {
                var1.loadUrl("javascript: " + var2);
            }

            return true;
        } else {
            return false;
        }
    }

    public void a(WebView var1, String var2, String var3) {
        if (var2 != null && !TextUtils.isEmpty(var3)) {
            this.c(var1, "(function() {this.omidVerificationProperties = this.omidVerificationProperties || {};Object.defineProperty(this.omidVerificationProperties, 'injectionId', {get: function() {var currentScript = document && document.currentScript;return currentScript && currentScript.getAttribute('data-injection-id');}, configurable: true});var script = document.createElement('script');script.setAttribute(\"type\",\"text/javascript\");script.setAttribute(\"src\",\"%SCRIPT_SRC%\");script.setAttribute(\"data-injection-id\",\"%INJECTION_ID%\");document.body.appendChild(script);})();".replace("%SCRIPT_SRC%", var2).replace("%INJECTION_ID%", var3));
        }

    }

    public void a(WebView var1, JSONObject var2) {
        g var10000 = this;
        Object[] var3;
        (var3 = new Object[1])[0] = var2;
        var10000.a(var1, "init", var3);
    }

    public void a(WebView var1, String var2, JSONObject var3, JSONObject var4, JSONObject var5) {
        g var10000 = this;
        Object[] var6;
        Object[] var10001 = var6 = new Object[4];
        var10001[0] = var2;
        var10001[1] = var3;
        var10001[2] = var4;
        var10001[3] = var5;
        var10000.a(var1, "startSession", var6);
    }

    public void a(WebView var1) {
        g var10000 = this;
        Object[] var2 = new Object[0];
        var10000.a(var1, "finishSession", var2);
    }

    public void a(WebView var1, String var2) {
        g var10000 = this;
        Object[] var3;
        (var3 = new Object[1])[0] = var2;
        var10000.a(var1, "setNativeViewHierarchy", var3);
    }

    public void b(WebView var1, String var2) {
        g var10000 = this;
        Object[] var3;
        (var3 = new Object[1])[0] = var2;
        var10000.a(var1, "setState", var3);
    }

    public void b(WebView var1) {
        g var10000 = this;
        Object[] var2 = new Object[0];
        var10000.a(var1, "publishImpressionEvent", var2);
    }

    public void c(WebView var1) {
        g var10000 = this;
        Object[] var2 = new Object[0];
        var10000.a(var1, "publishLoadedEvent", var2);
    }

    public void b(WebView var1, @NonNull JSONObject var2) {
        g var10000 = this;
        Object[] var3;
        (var3 = new Object[1])[0] = var2;
        var10000.a(var1, "publishLoadedEvent", var3);
    }

    public void a(WebView var1, ErrorType var2, String var3) {
        g var10000 = this;
        Object[] var4;
        Object[] var10001 = var4 = new Object[2];
        var10001[0] = var2.toString();
        var10001[1] = var3;
        var10000.a(var1, "error", var4);
    }

    public void a(WebView var1, String var2, JSONObject var3) {
        g var10000;
        Object[] var4;
        if (var3 != null) {
            var10000 = this;
            Object[] var10001 = var4 = new Object[2];
            var10001[0] = var2;
            var10001[1] = var3;
            var10000.a(var1, "publishMediaEvent", var4);
        } else {
            var10000 = this;
            (var4 = new Object[1])[0] = var2;
            var10000.a(var1, "publishMediaEvent", var4);
        }

    }

    public void a(WebView var1, float var2) {
        g var10000 = this;
        Object[] var3;
        (var3 = new Object[1])[0] = var2;
        var10000.a(var1, "setDeviceVolume", var3);
    }

    public void c(WebView var1, @NonNull JSONObject var2) {
        g var10000 = this;
        Object[] var3;
        (var3 = new Object[1])[0] = var2;
        var10000.a(var1, "setLastActivity", var3);
    }

    @VisibleForTesting
    void a(WebView var1, String var2, Object... var3) {
        if (var1 != null) {
            StringBuilder var4;
            StringBuilder var10001 = var4 = new StringBuilder(128);
            var4.append("if(window.omidBridge!==undefined){omidBridge.");
            var4.append(var2);
            var4.append("(");
            this.a(var4, var3);
            var10001.append(")}");
            this.a(var1, var4);
        } else {
            d.a("The WebView is null for " + var2);
        }

    }

    @VisibleForTesting
    void a(WebView var1, StringBuilder var2) {
        String var4 = var2.toString();
        Handler var3;
        if ((var3 = var1.getHandler()) != null && Looper.myLooper() != var3.getLooper()) {
            Handler var10000 = var3;
            Runnable var5 = new Runnable() {
                @Override
                public void run() {
                    g var10000 = g.this;
//                    WebView var1 = this.a;
                    var10000.c(var1, var4);
                }
            };
//            var5 = new Runnable() {
//                {
//                    this.a = var2;
//                    this.b = var3;
//                }
//
//                public void run() {
//                    g var10000 = g.this;
//                    <undefinedtype> var10001 = this;
//                    WebView var1 = this.a;
//                    var10000.c(var1, var10001.b);
//                }
//            }(var1, var4);
//
            var10000.post(var5);
        } else {
            this.c(var1, var4);
        }

    }

    @VisibleForTesting
    void a(StringBuilder var1, Object[] var2) {
        if (var2 != null && var2.length > 0) {
            int var5 = var2.length;

            for(int var3 = 0; var3 < var5; ++var3) {
                Object var4;
                if ((var4 = var2[var3]) == null) {
                    var1.append('"').append('"');
                } else if (var4 instanceof String) {
                    String var6;
                    if ((var6 = var4.toString()).startsWith("{")) {
                        var1.append(var6);
                    } else {
                        var1.append('"').append(var6).append('"');
                    }
                } else {
                    var1.append(var4);
                }

                var1.append(",");
            }

            var1.setLength(var1.length() - 1);
        }

    }
}
