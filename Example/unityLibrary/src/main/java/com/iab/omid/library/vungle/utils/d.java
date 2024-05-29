//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.utils;

import android.text.TextUtils;
import android.util.Log;
import com.iab.omid.library.vungle.a;

public final class d {
    public static void a(String var0) {
        if (a.a && !TextUtils.isEmpty(var0)) {
            Log.i("OMIDLIB", var0);
        }

    }

    public static void a(String var0, Exception var1) {
        if (a.a && !TextUtils.isEmpty(var0) || var1 != null) {
            Log.e("OMIDLIB", var0, var1);
        }

    }
}
