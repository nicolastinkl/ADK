//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle;

import android.content.Context;

public final class Omid {
    private static b INSTANCE = new b();

    private Omid() {
    }

    public static void activate(Context var0) {
        INSTANCE.a(var0.getApplicationContext());
    }

    public static void updateLastActivity() {
        INSTANCE.c();
    }

    public static String getVersion() {
        return INSTANCE.a();
    }

    public static boolean isActive() {
        return INSTANCE.b();
    }
}
