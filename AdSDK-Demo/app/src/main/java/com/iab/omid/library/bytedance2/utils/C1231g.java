package com.iab.omid.library.bytedance2.utils;

import android.text.TextUtils;
import com.iab.omid.library.bytedance2.Omid;
import com.iab.omid.library.bytedance2.adsession.C1195a;
import com.iab.omid.library.bytedance2.adsession.CreativeType;
import com.iab.omid.library.bytedance2.adsession.ImpressionType;
import com.iab.omid.library.bytedance2.adsession.Owner;

/* renamed from: com.iab.omid.library.bytedance2.utils.g */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/utils/g.class */
public class C1231g {
    /* renamed from: a */
    public static void m71a() {
        if (!Omid.isActive()) {
            throw new IllegalStateException("Method called before OM SDK activation");
        }
    }

    /* renamed from: a */
    public static void m68a(Object obj, String str) {
        if (obj == null) {
            throw new IllegalArgumentException(str);
        }
    }

    /* renamed from: a */
    public static void m66a(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException(str2);
        }
    }

    /* renamed from: a */
    public static void m67a(String str, int i, String str2) {
        if (str.length() > i) {
            throw new IllegalArgumentException(str2);
        }
    }

    /* renamed from: c */
    public static void m64c(C1195a c1195a) {
        if (c1195a.m218j()) {
            throw new IllegalStateException("AdSession is started");
        }
    }

    /* renamed from: d */
    private static void m63d(C1195a c1195a) {
        if (!c1195a.m218j()) {
            throw new IllegalStateException("AdSession is not started");
        }
    }

    /* renamed from: b */
    public static void m65b(C1195a c1195a) {
        if (c1195a.m221g()) {
            throw new IllegalStateException("AdSession is finished");
        }
    }

    /* renamed from: a */
    public static void m69a(C1195a c1195a) {
        m63d(c1195a);
        m65b(c1195a);
    }

    /* renamed from: g */
    public static void m60g(C1195a c1195a) {
        if (c1195a.getAdSessionStatePublisher().m116c() != null) {
            throw new IllegalStateException("AdEvents already exists for AdSession");
        }
    }

    /* renamed from: h */
    public static void m59h(C1195a c1195a) {
        if (c1195a.getAdSessionStatePublisher().m115d() != null) {
            throw new IllegalStateException("MediaEvents already exists for AdSession");
        }
    }

    /* renamed from: e */
    public static void m62e(C1195a c1195a) {
        if (!c1195a.m220h()) {
            throw new IllegalStateException("Impression event is not expected from the Native AdSession");
        }
    }

    /* renamed from: f */
    public static void m61f(C1195a c1195a) {
        if (!c1195a.m219i()) {
            throw new IllegalStateException("Cannot create MediaEvents for JavaScript AdSession");
        }
    }

    /* renamed from: a */
    public static void m70a(Owner owner, CreativeType creativeType, ImpressionType impressionType) {
        if (owner == Owner.NONE) {
            throw new IllegalArgumentException("Impression owner is none");
        }
        if (creativeType == CreativeType.DEFINED_BY_JAVASCRIPT && owner == Owner.NATIVE) {
            throw new IllegalArgumentException("ImpressionType/CreativeType can only be defined as DEFINED_BY_JAVASCRIPT if Impression Owner is JavaScript");
        }
        if (impressionType == ImpressionType.DEFINED_BY_JAVASCRIPT && owner == Owner.NATIVE) {
            throw new IllegalArgumentException("ImpressionType/CreativeType can only be defined as DEFINED_BY_JAVASCRIPT if Impression Owner is JavaScript");
        }
    }
}
