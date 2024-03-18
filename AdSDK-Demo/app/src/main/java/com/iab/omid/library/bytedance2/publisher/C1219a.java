package com.iab.omid.library.bytedance2.publisher;

import android.annotation.SuppressLint;
import android.webkit.WebView;

/* renamed from: com.iab.omid.library.bytedance2.publisher.a */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/publisher/a.class */
public class C1219a extends AdSessionStatePublisher {
    @SuppressLint({"SetJavaScriptEnabled"})
    public C1219a(WebView webView) {
        if (webView != null && !webView.getSettings().getJavaScriptEnabled()) {
            webView.getSettings().setJavaScriptEnabled(true);
        }
        m129a(webView);
    }
}
