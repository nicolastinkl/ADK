//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.internal;

import com.iab.omid.library.vungle.adsession.a;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class c {
    private static c c = new c();
    private ArrayList<a> a;
    private  ArrayList<a> b;

    private c() {
        c var10000 = this;
        c var10001 = this;
//        super();
        ArrayList var1;
        var1 = new ArrayList();
        var10001.a = var1;
        var1 = new ArrayList();
        var10000.b = var1;
    }

    public static c c() {
        return c;
    }

    public void a(a var1) {
        this.a.add(var1);
    }

    public void c(a var1) {
        boolean var10000 = this.d();
        this.b.add(var1);
        if (!var10000) {
            h.c().d();
        }

    }

    public void b(a var1) {
        boolean var10000 = this.d();
        this.a.remove(var1);
        this.b.remove(var1);
        if (var10000 && !this.d()) {
            h.c().e();
        }

    }

    public Collection<a> b() {
        return Collections.unmodifiableCollection(this.a);
    }

    public Collection<a> a() {
        return Collections.unmodifiableCollection(this.b);
    }

    public boolean d() {
        return this.b.size() > 0;
    }
}
