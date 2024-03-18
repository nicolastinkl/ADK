package com.iab.omid.library.bytedance2.utils;

import android.os.Build;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.utils.b */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/utils/b.class */
public final class C1223b {
    /* renamed from: a */
    public static String m103a() {
        return Build.MANUFACTURER + "; " + Build.MODEL;
    }

    /* renamed from: c */
    public static String m101c() {
        return Integer.toString(Build.VERSION.SDK_INT);
    }

    /* renamed from: b */
    public static String m102b() {
        return "Android";
    }

    /* renamed from: d */
    public static JSONObject m100d() {
        JSONObject jSONObject = new JSONObject();
        C1224c.m89a(jSONObject, "deviceType", m103a());
        C1224c.m89a(jSONObject, "osVersion", m101c());
        C1224c.m89a(jSONObject, "os", m102b());
        return jSONObject;
    }
}
