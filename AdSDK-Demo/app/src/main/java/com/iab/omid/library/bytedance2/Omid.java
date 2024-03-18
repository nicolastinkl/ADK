package com.iab.omid.library.bytedance2;

import android.content.Context;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/Omid.class */
public final class Omid {
    private static C1196b INSTANCE = new C1196b();

    private Omid() {
    }

    public static void activate(Context context) {
        INSTANCE.m213a(context.getApplicationContext());
    }

    public static void updateLastActivity() {
        INSTANCE.m209c();
    }

    public static String getVersion() {
        return INSTANCE.m214a();
    }

    public static boolean isActive() {
        return INSTANCE.m211b();
    }
}
