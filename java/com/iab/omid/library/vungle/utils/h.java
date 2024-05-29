//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.utils;

import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewParent;

public final class h {
    public static float c(View var0) {
        return VERSION.SDK_INT >= 21 ? var0.getZ() : 0.0F;
    }

    public static View b(View var0) {
        ViewParent var1;
        return (var1 = var0.getParent()) instanceof View ? (View)var1 : null;
    }

    public static boolean e(View var0) {
        if (VERSION.SDK_INT >= 19 && !var0.isAttachedToWindow()) {
            return false;
        } else if (!var0.isShown()) {
            return false;
        } else {
            while(var0 != null) {
                if (var0.getAlpha() == 0.0F) {
                    return false;
                }

                var0 = b(var0);
            }

            return true;
        }
    }

    public static boolean d(View var0) {
        return a(var0) == null;
    }

    public static String a(View var0) {
        if (VERSION.SDK_INT >= 19 && !var0.isAttachedToWindow()) {
            return "notAttached";
        } else {
            int var1;
            if ((var1 = var0.getVisibility()) == 8) {
                return "viewGone";
            } else if (var1 == 4) {
                return "viewInvisible";
            } else if (var1 != 0) {
                return "viewNotVisible";
            } else {
                return var0.getAlpha() == 0.0F ? "viewAlphaZero" : null;
            }
        }
    }
}
