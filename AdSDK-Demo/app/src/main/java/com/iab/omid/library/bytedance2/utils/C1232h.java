package com.iab.omid.library.bytedance2.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewParent;

/* renamed from: com.iab.omid.library.bytedance2.utils.h */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/utils/h.class */
public final class C1232h {
    /* renamed from: c */
    public static float m56c(View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            return view.getZ();
        }
        return 0.0f;
    }

    /* renamed from: b */
    public static View m57b(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            return (View) parent;
        }
        return null;
    }

    /* renamed from: e */
    public static boolean m54e(View view) {
        if ((Build.VERSION.SDK_INT < 19 || view.isAttachedToWindow()) && view.isShown()) {
            while (view != null) {
                if (view.getAlpha() == 0.0f) {
                    return false;
                }
                view = m57b(view);
            }
            return true;
        }
        return false;
    }

    /* renamed from: d */
    public static boolean m55d(View view) {
        return m58a(view) == null;
    }

    /* renamed from: a */
    public static String m58a(View view) {
        if (Build.VERSION.SDK_INT < 19 || view.isAttachedToWindow()) {
            int visibility = view.getVisibility();
            if (visibility == 8) {
                return "viewGone";
            }
            if (visibility == 4) {
                return "viewInvisible";
            }
            if (visibility != 0) {
                return "viewNotVisible";
            }
            if (view.getAlpha() == 0.0f) {
                return "viewAlphaZero";
            }
            return null;
        }
        return "notAttached";
    }
}
