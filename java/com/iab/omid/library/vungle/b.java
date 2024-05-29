//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle;

import android.content.Context;
import com.iab.omid.library.vungle.internal.f;
import com.iab.omid.library.vungle.internal.h;
import com.iab.omid.library.vungle.utils.a;
import com.iab.omid.library.vungle.utils.c;
import com.iab.omid.library.vungle.utils.e;
import com.iab.omid.library.vungle.utils.g;

public class b {
    private boolean a;

    public b() {
    }

    private void b(Context var1) {
        g.a(var1, "Application Context cannot be null");
    }

    String a() {
        return "1.4.8-Vungle";
    }

    boolean b() {
        return this.a;
    }

    void a(boolean var1) {
        this.a = var1;
    }

    void a(Context var1) {
        this.b(var1);
        if (!this.b()) {
            this.a(true);
            h.c().a(var1);
            com.iab.omid.library.vungle.internal.b.g().a(var1);
            com.iab.omid.library.vungle.utils.a.a(var1);
            c.a(var1);
            e.a(var1);
            f.b().a(var1);
            com.iab.omid.library.vungle.internal.a.a().a(var1);
        }

    }

    void c() {
        g.a();
        com.iab.omid.library.vungle.internal.a.a().d();
    }
}
