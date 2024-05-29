//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.walking;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.annotation.VisibleForTesting;
import com.iab.omid.library.vungle.processor.a;
import com.iab.omid.library.vungle.processor.b;
import com.iab.omid.library.vungle.utils.f;
import com.iab.omid.library.vungle.utils.h;
import com.iab.omid.library.vungle.walking.async.c;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class TreeWalker implements com.iab.omid.library.vungle.processor.a {
    private static TreeWalker i = new TreeWalker();
    private static Handler j = new Handler(Looper.getMainLooper());
    private static Handler k = null;
    private static final Runnable l = new Runnable() {
        public void run() {
            TreeWalker.getInstance().l();
        }
    };
    private static final Runnable m = new Runnable() {
        public void run() {
            if (TreeWalker.k != null) {
                TreeWalker.k.post(TreeWalker.l);
                TreeWalker.k.postDelayed(TreeWalker.m, 200L);
            }

        }
    };
    private List<TreeWalkerTimeLogger> a;
    private int b;
    private boolean c;
    private  List<com.iab.omid.library.vungle.weakreference.a> d;
    private b e;
    private com.iab.omid.library.vungle.walking.a f;
    private com.iab.omid.library.vungle.walking.b g;
    private long h;

    TreeWalker() {
        TreeWalker var10000 = this;
        TreeWalker var10001 = this;
        TreeWalker var10002 = this;
        TreeWalker var10003 = this;
        TreeWalker var10004 = this;
        TreeWalker var10005 = this;

        ArrayList var2;
        var2 = new ArrayList();
        var10005.a = var2;
        var10004.c = false;
        var2 = new ArrayList();
        var10003.d = var2;
        com.iab.omid.library.vungle.walking.a var3;
        var3 = new com.iab.omid.library.vungle.walking.a();
        var10002.f = var3;
        b var4;
        var4 = new b();
        var10001.e = var4;
        com.iab.omid.library.vungle.walking.b var5;

        c var1;
        var1 = new c();
        com.iab.omid.library.vungle.walking.b var6 = var5 = new com.iab.omid.library.vungle.walking.b(var1);

        var10000.g = var5;
    }

    public static TreeWalker getInstance() {
        return i;
    }

    private void l() {
        this.e();
        this.f();
        this.d();
    }

    private void e() {
        this.b = 0;
        this.d.clear();
        this.c = false;
        Iterator var1 = com.iab.omid.library.vungle.internal.c.c().a().iterator();

        while(var1.hasNext()) {
            if (((com.iab.omid.library.vungle.adsession.a)var1.next()).e()) {
                this.c = true;
                break;
            }
        }

        this.h = com.iab.omid.library.vungle.utils.f.b();
    }

    private void d() {
        this.a(com.iab.omid.library.vungle.utils.f.b() - this.h);
    }

    private void a(View var1, a var2, JSONObject var3, com.iab.omid.library.vungle.walking.c var4, boolean var5) {
        boolean var6;
        if (var4 == com.iab.omid.library.vungle.walking.c.a) {
            var6 = true;
        } else {
            var6 = false;
        }

        var2.a(var1, var3, this, var6, var5);
    }

    private boolean b(View var1, JSONObject var2) {
        String var3;
        if ((var3 = this.f.d(var1)) != null) {
            com.iab.omid.library.vungle.utils.c.a(var2, var3);
            com.iab.omid.library.vungle.utils.c.a(var2, this.f.f(var1));
            this.f.d();
            return true;
        } else {
            return false;
        }
    }

    private void a(String var1, View var2, JSONObject var3) {
        TreeWalker var10000 = this;
        a var5 = this.e.b();
        String var4;
        if ((var4 = var10000.f.b(var1)) != null) {
            JSONObject var6;
            JSONObject var10001 = var6 = var5.a(var2);
            com.iab.omid.library.vungle.utils.c.a(var10001, var1);
            com.iab.omid.library.vungle.utils.c.b(var10001, var4);
            com.iab.omid.library.vungle.utils.c.a(var3, var6);
        }

    }

    private boolean a(View var1, JSONObject var2) {
        com.iab.omid.library.vungle.walking.a.a1 var3;
        if ((var3 = this.f.c(var1)) != null) {
            com.iab.omid.library.vungle.utils.c.a(var2, var3);
            return true;
        } else {
            return false;
        }
    }

    private void a(long var1) {
        if (this.a.size() > 0) {
            Iterator var3 = this.a.iterator();

            while(var3.hasNext()) {
                TreeWalkerTimeLogger var4;
                TreeWalkerTimeLogger var10000 = var4 = (TreeWalkerTimeLogger)var3.next();
                var10000.onTreeProcessed(this.b, TimeUnit.NANOSECONDS.toMillis(var1));
                if (var10000 instanceof TreeWalkerNanoTimeLogger) {
                    ((TreeWalkerNanoTimeLogger)var4).onTreeProcessedNano(this.b, var1);
                }
            }
        }

    }

    private void i() {
        if (k == null) {
            (k = new Handler(Looper.getMainLooper())).post(l);
            k.postDelayed(m, 200L);
        }

    }

    private void k() {
        Handler var1;
        if ((var1 = k) != null) {
            var1.removeCallbacks(m);
            k = null;
        }

    }

    public void addTimeLogger(TreeWalkerTimeLogger var1) {
        if (!this.a.contains(var1)) {
            this.a.add(var1);
        }

    }

    public void removeTimeLogger(TreeWalkerTimeLogger var1) {
        if (this.a.contains(var1)) {
            this.a.remove(var1);
        }

    }

    public void h() {
        this.i();
    }

    public void j() {
        this.g();
        this.a.clear();
        j.post(new Runnable() {
            public void run() {
                TreeWalker.this.g.b();
            }
        });
    }

    public void g() {
        this.k();
    }

    @VisibleForTesting
    void f() {
        this.f.e();
        long var1 = com.iab.omid.library.vungle.utils.f.b();
        a var3 = this.e.a();
        if (this.f.b().size() > 0) {
            Iterator var4 = this.f.b().iterator();

            while(var4.hasNext()) {
                String var5 = (String)var4.next();
                JSONObject var6;
                JSONObject var10001 = var6 = var3.a((View)null);
                this.a(var5, this.f.a(var5), var6);
                com.iab.omid.library.vungle.utils.c.b(var10001);
                HashSet var7;
                HashSet var11 = var7 = new HashSet();
                var11.add(var5);
                this.g.a(var6, var7, var1);
            }
        }

        if (this.f.c().size() > 0) {
            JSONObject var9;
            JSONObject var10002 = var9 = var3.a((View)null);
            com.iab.omid.library.vungle.walking.c var10 = com.iab.omid.library.vungle.walking.c.a;
            this.a((View)null, var3, var9, var10, false);
            com.iab.omid.library.vungle.utils.c.b(var10002);
            this.g.b(var9, this.f.c(), var1);
            if (this.c) {
                Iterator var8 = com.iab.omid.library.vungle.internal.c.c().a().iterator();

                while(var8.hasNext()) {
                    ((com.iab.omid.library.vungle.adsession.a)var8.next()).a(this.d);
                }
            }
        } else {
            this.g.b();
        }

        this.f.a();
    }

    @Override
    public JSONObject a(View var1) {
        return null;
    }

    @Override
    public void a(View var1, JSONObject var2, com.iab.omid.library.vungle.processor.a var3, boolean var4, boolean var5) {

    }

    public void a(View var1, a var2, JSONObject var3, boolean var4) {
        if (com.iab.omid.library.vungle.utils.h.d(var1)) {
            com.iab.omid.library.vungle.walking.c var5;
            if ((var5 = this.f.e(var1)) != com.iab.omid.library.vungle.walking.c.c) {
                com.iab.omid.library.vungle.utils.c.a(var3, var3 = var2.a(var1));
                if (!this.b(var1, var3)) {
                    boolean var10000 = var4;
                    var4 = this.a(var1, var3);
                    if (!var10000 && !var4) {
                        var4 = false;
                    } else {
                        var4 = true;
                    }

                    if (this.c && var5 == com.iab.omid.library.vungle.walking.c.b && !var4) {
                        this.d.add(new com.iab.omid.library.vungle.weakreference.a(var1));
                    }

                    this.a(var1, var2, var3, var5, var4);
                }

                ++this.b;
            }
        }
    }

    public interface TreeWalkerNanoTimeLogger extends TreeWalkerTimeLogger {
        void onTreeProcessedNano(int var1, long var2);
    }

    public interface TreeWalkerTimeLogger {
        void onTreeProcessed(int var1, long var2);
    }
}
