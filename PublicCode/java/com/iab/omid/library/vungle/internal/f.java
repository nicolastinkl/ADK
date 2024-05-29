//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.internal;

import android.annotation.SuppressLint;
import android.content.Context;

public class f {
    @SuppressLint({"StaticFieldLeak"})
    private static f b = new f();
    private Context a;

    public static f b() {
        return b;
    }

    private f() {
    }

    public Context a() {
        return this.a;
    }

    public void a(Context var1) {
        if (var1 != null) {
            var1 = var1.getApplicationContext();
        } else {
            var1 = null;
        }

        this.a = var1;
    }
}
