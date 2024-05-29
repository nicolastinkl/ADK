//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.walking.async;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class c implements b.a {
    private final BlockingQueue<Runnable> a;
    private final ThreadPoolExecutor b;
    private final ArrayDeque<b> c;
    private b d;


    public c() {
        super();
        this.c = new ArrayDeque<>();
        this.a = new LinkedBlockingQueue<>();
        this.b = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.SECONDS, this.a);
    }


/*
    public c() {
        c var10000 = this;
        c var10001 = this;
        c var10002 = this;
        c var10003 = this;
        super();
        ArrayDeque var3;
        var3 = new ArrayDeque();
        var10003.c = var3;
        var10002.d = null;
        LinkedBlockingQueue var4;
        var4 = new LinkedBlockingQueue();
        var10001.a = var4;
        ThreadPoolExecutor var1;
        ThreadPoolExecutor var5 = var1 = new ThreadPoolExecutor;
        TimeUnit var2 = TimeUnit.SECONDS;
        var5(1, 1, 1L, var2, var4);
        var10000.b = var1;
    }
*/

    private void a() {
        b var1;
        b var10000 = var1 = (b)this.c.poll();
        this.d = var1;
        if (var10000 != null) {
            var1.a(this.b);
        }

    }

    public void b(b var1) {
        var1.a(this);
        this.c.add(var1);
        if (this.d == null) {
            this.a();
        }

    }

    public void a(b var1) {
        this.d = null;
        this.a();
    }
}
