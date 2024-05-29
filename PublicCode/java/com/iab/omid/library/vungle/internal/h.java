//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.internal;

import android.content.Context;
import android.os.Handler;
import com.iab.omid.library.vungle.devicevolume.a;
import com.iab.omid.library.vungle.devicevolume.b;
import com.iab.omid.library.vungle.devicevolume.c;
import com.iab.omid.library.vungle.devicevolume.e;
import com.iab.omid.library.vungle.walking.TreeWalker;
import java.util.Iterator;

public class h implements d.a, c {
    private static h f;
    private float a = 0.0F;
    private final e b;
    private final b c;
    private com.iab.omid.library.vungle.devicevolume.d d;
    private com.iab.omid.library.vungle.internal.c e;

    public h(e var1, b var2) {
        this.b = var1;
        this.c = var2;
    }

    public static h c() {
        if (f == null) {
            b var0;
            var0 = new b();
            e var1;
            var1 = new e();
            f = new h(var1, var0);
        }

        return f;
    }

    private com.iab.omid.library.vungle.internal.c a() {
        if (this.e == null) {
            this.e = com.iab.omid.library.vungle.internal.c.c();
        }

        return this.e;
    }

    public void a(Context var1) {
        a var2 = this.c.a();
        this.d = this.b.a(new Handler(), var1, var2, this);
    }

    public void d() {
        com.iab.omid.library.vungle.internal.b.g().a(this);
        com.iab.omid.library.vungle.internal.b.g().e();
        TreeWalker.getInstance().h();
        this.d.c();
    }

    public void e() {
        TreeWalker.getInstance().j();
        com.iab.omid.library.vungle.internal.b.g().f();
        this.d.d();
    }

    public void a(boolean var1) {
        if (var1) {
            TreeWalker.getInstance().h();
        } else {
            TreeWalker.getInstance().g();
        }

    }

    public void a(float var1) {
        this.a = var1;
        Iterator var2 = this.a().a().iterator();

        while(var2.hasNext()) {
            ((com.iab.omid.library.vungle.adsession.a)var2.next()).getAdSessionStatePublisher().a(var1);
        }

    }

    public float b() {
        return this.a;
    }
}
