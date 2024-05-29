//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.utils;

import android.os.Build;
import android.os.Build.VERSION;
import org.json.JSONObject;

public final class b {
    public static String a() {
        return Build.MANUFACTURER + "; " + Build.MODEL;
    }

    public static String c() {
        return Integer.toString(VERSION.SDK_INT);
    }

    public static String b() {
        return "Android";
    }

    public static JSONObject d() {
        JSONObject var10000 = new JSONObject();
        c.a(var10000, "deviceType", a());
        c.a(var10000, "osVersion", c());
        c.a(var10000, "os", b());
        return var10000;
    }
}
