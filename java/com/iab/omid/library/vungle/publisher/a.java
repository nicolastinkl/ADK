//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.publisher;

import android.annotation.SuppressLint;
import android.webkit.WebView;

public class a extends AdSessionStatePublisher {
    @SuppressLint({"SetJavaScriptEnabled"})
    public a(WebView var1) {
        if (var1 != null && !var1.getSettings().getJavaScriptEnabled()) {
            var1.getSettings().setJavaScriptEnabled(true);
        }

        this.a(var1);
    }
}
