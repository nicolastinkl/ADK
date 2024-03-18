//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.internal;

import android.content.Context;
import androidx.annotation.NonNull;
import com.iab.omid.library.vungle.utils.f;
import java.util.Date;
import java.util.Iterator;

public class a implements d.a {
    private static a f;
    protected f a;
    private Date b;
    private boolean c;
    private d d;
    private boolean e;

    private a(d var1) {
        a var10000 = this;
        a var10001 = this;
//        super();
        f var2;
        var2 = new f();
        var10001.a = var2;
        var10000.d = var1;
    }

    public static a a() {
        return f;
    }

    private void c() {
        if (this.c && this.b != null) {
            Iterator var1 = com.iab.omid.library.vungle.internal.c.c().a().iterator();

            while(var1.hasNext()) {
                ((com.iab.omid.library.vungle.adsession.a)var1.next()).getAdSessionStatePublisher().a(this.b());
            }

        }
    }

    static {
        d var0;
        var0 = new d();
        f = new a(var0);
    }

    public Date b() {
        Date var1;
        return (var1 = this.b) != null ? (Date)var1.clone() : null;
    }

    public void a(@NonNull Context var1) {
        if (!this.c) {
            this.d.a(var1);
            this.d.a(this);
            this.d.e();
            this.e = this.d.c();
            this.c = true;
        }

    }

    public void a(boolean var1) {
        if (!this.e && var1) {
            this.d();
        }

        this.e = var1;
    }

    public void d() {
        Date var1 = this.a.a();
        Date var2;
        if ((var2 = this.b) == null || var1.after(var2)) {
            this.b = var1;
            this.c();
        }

    }
}
