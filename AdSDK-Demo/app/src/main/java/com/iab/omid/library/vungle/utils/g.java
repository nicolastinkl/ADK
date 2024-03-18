//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.utils;

import android.text.TextUtils;
import com.iab.omid.library.vungle.Omid;
import com.iab.omid.library.vungle.adsession.CreativeType;
import com.iab.omid.library.vungle.adsession.ImpressionType;
import com.iab.omid.library.vungle.adsession.Owner;
import com.iab.omid.library.vungle.adsession.a;

public class g {
    public static void a() {
        if (!Omid.isActive()) {
            throw new IllegalStateException("Method called before OM SDK activation");
        }
    }

    public static void a(Object var0, String var1) {
        if (var0 == null) {
            throw new IllegalArgumentException(var1);
        }
    }

    public static void a(String var0, String var1) {
        if (TextUtils.isEmpty(var0)) {
            throw new IllegalArgumentException(var1);
        }
    }

    public static void a(String var0, int var1, String var2) {
        if (var0.length() > var1) {
            throw new IllegalArgumentException(var2);
        }
    }

    public static void c(a var0) {
        if (var0.j()) {
            throw new IllegalStateException("AdSession is started");
        }
    }

    private static void d(a var0) {
        if (!var0.j()) {
            throw new IllegalStateException("AdSession is not started");
        }
    }

    public static void b(a var0) {
        if (var0.g()) {
            throw new IllegalStateException("AdSession is finished");
        }
    }

    public static void a(a var0) {
        d(var0);
        b(var0);
    }

    public static void g(a var0) {
        if (var0.getAdSessionStatePublisher().c() != null) {
            throw new IllegalStateException("AdEvents already exists for AdSession");
        }
    }

    public static void h(a var0) {
        if (var0.getAdSessionStatePublisher().d() != null) {
            throw new IllegalStateException("MediaEvents already exists for AdSession");
        }
    }

    public static void e(a var0) {
        if (!var0.h()) {
            throw new IllegalStateException("Impression event is not expected from the Native AdSession");
        }
    }

    public static void f(a var0) {
        if (!var0.i()) {
            throw new IllegalStateException("Cannot create MediaEvents for JavaScript AdSession");
        }
    }

    public static void a(Owner var0, CreativeType var1, ImpressionType var2) {
        if (var0 != Owner.NONE) {
            if (var1 == CreativeType.DEFINED_BY_JAVASCRIPT && var0 == Owner.NATIVE) {
                throw new IllegalArgumentException("ImpressionType/CreativeType can only be defined as DEFINED_BY_JAVASCRIPT if Impression Owner is JavaScript");
            } else if (var2 == ImpressionType.DEFINED_BY_JAVASCRIPT && var0 == Owner.NATIVE) {
                throw new IllegalArgumentException("ImpressionType/CreativeType can only be defined as DEFINED_BY_JAVASCRIPT if Impression Owner is JavaScript");
            }
        } else {
            throw new IllegalArgumentException("Impression owner is none");
        }
    }
}
