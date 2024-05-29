//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.walking;

import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewParent;
import com.iab.omid.library.vungle.internal.c;
import com.iab.omid.library.vungle.internal.e;
import com.iab.omid.library.vungle.utils.h;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class a {
    private  HashMap<View, String> a;
    private  HashMap<View, com.iab.omid.library.vungle.walking.a.a1> b;
    private  HashMap<String, View> c;
    private  HashSet<View> d;
    private  HashSet<String> e;
    private  HashSet<String> f;
    private  HashMap<String, String> g;
    private  Map<View, Boolean> h;
    private boolean i;

    public a() {
        com.iab.omid.library.vungle.walking.a var10000 = this;
        com.iab.omid.library.vungle.walking.a var10001 = this;
        com.iab.omid.library.vungle.walking.a var10002 = this;
        com.iab.omid.library.vungle.walking.a var10003 = this;
        com.iab.omid.library.vungle.walking.a var10004 = this;
        com.iab.omid.library.vungle.walking.a var10005 = this;
        com.iab.omid.library.vungle.walking.a var10006 = this;
        com.iab.omid.library.vungle.walking.a var10007 = this;

        HashMap var1;
        var1 = new HashMap();
        var10007.a = var1;
        var1 = new HashMap();
        var10006.b = var1;
        var1 = new HashMap();
        var10005.c = var1;
        HashSet var2;
        var2 = new HashSet();
        var10004.d = var2;
        var2 = new HashSet();
        var10003.e = var2;
        var2 = new HashSet();
        var10002.f = var2;
        var1 = new HashMap();
        var10001.g = var1;
        WeakHashMap var3;
        var3 = new WeakHashMap();
        var10000.h = var3;
    }

    private String a(View var1) {
        if (VERSION.SDK_INT >= 19 && !var1.isAttachedToWindow()) {
            return "notAttached";
        } else if (this.b(var1)) {
            return "noWindowFocus";
        } else {
            HashSet var2;
            var2 = new HashSet();

            while(var1 != null) {
                String var3;
                if ((var3 = com.iab.omid.library.vungle.utils.h.a(var1)) != null) {
                    return var3;
                }

                var2.add(var1);
                ViewParent var4;
                if ((var4 = var1.getParent()) instanceof View) {
                    var1 = (View)var4;
                } else {
                    var1 = null;
                }
            }

            this.d.addAll(var2);
            return null;
        }
    }

    private Boolean b(View var1) {
        if (var1.hasWindowFocus()) {
            this.h.remove(var1);
            return Boolean.FALSE;
        } else if (this.h.containsKey(var1)) {
            return (Boolean)this.h.get(var1);
        } else {
            Map var3 = this.h;
            Boolean var2;
            Boolean var10000 = var2 = Boolean.FALSE;
            var3.put(var1, var2);
            return var10000;
        }
    }

    private void a(com.iab.omid.library.vungle.adsession.a var1) {
        Iterator var2 = var1.d().iterator();

        while(var2.hasNext()) {
            this.a((e)var2.next(), var1);
        }

    }

    private void a(e var1, com.iab.omid.library.vungle.adsession.a var2) {
        View var3;
        if ((var3 = (View)var1.c().get()) != null) {
            com.iab.omid.library.vungle.walking.a.a1 var4;
            if ((var4 = (com.iab.omid.library.vungle.walking.a.a1)this.b.get(var3)) != null) {
                var4.a(var2.getAdSessionId());
            } else {
                this.b.put(var3, new com.iab.omid.library.vungle.walking.a.a1(var1, var2.getAdSessionId()));
            }

        }
    }

    public HashSet<String> c() {
        return this.e;
    }

    public HashSet<String> b() {
        return this.f;
    }

    public String b(String var1) {
        return (String)this.g.get(var1);
    }

    public void e() {
        c var1;
        if ((var1 = com.iab.omid.library.vungle.internal.c.c()) != null) {
            Iterator var6 = var1.a().iterator();

            while(var6.hasNext()) {
                com.iab.omid.library.vungle.adsession.a var2;
                com.iab.omid.library.vungle.adsession.a var10000 = var2 = (com.iab.omid.library.vungle.adsession.a)var6.next();
                View var3 = var10000.c();
                if (var10000.f()) {
                    String var4 = var2.getAdSessionId();
                    if (var3 != null) {
                        String var5;
                        if ((var5 = this.a(var3)) == null) {
                            this.e.add(var4);
                            this.a.put(var3, var4);
                            this.a(var2);
                        } else if (var5 != "noWindowFocus") {
                            this.f.add(var4);
                            this.c.put(var4, var3);
                            this.g.put(var4, var5);
                        }
                    } else {
                        this.f.add(var4);
                        this.g.put(var4, "noAdView");
                    }
                }
            }
        }

    }

    public void a() {
        this.a.clear();
        this.b.clear();
        this.c.clear();
        this.d.clear();
        this.e.clear();
        this.f.clear();
        this.g.clear();
        this.i = false;
    }

    public void d() {
        this.i = true;
    }

    public String d(View var1) {
        if (this.a.size() == 0) {
            return null;
        } else {
            String var2;
            if ((var2 = (String)this.a.get(var1)) != null) {
                this.a.remove(var1);
            }

            return var2;
        }
    }

    public View a(String var1) {
        return (View)this.c.get(var1);
    }

    public com.iab.omid.library.vungle.walking.a.a1 c(View var1) {
        com.iab.omid.library.vungle.walking.a.a1 var2;
        if ((var2 = (com.iab.omid.library.vungle.walking.a.a1)this.b.get(var1)) != null) {
            this.b.remove(var1);
        }

        return var2;
    }

    public com.iab.omid.library.vungle.walking.c e(View var1) {
        if (this.d.contains(var1)) {
            return com.iab.omid.library.vungle.walking.c.a;
        } else {
            return this.i ? com.iab.omid.library.vungle.walking.c.b : com.iab.omid.library.vungle.walking.c.c;
        }
    }

    public boolean f(View var1) {
        if (this.h.containsKey(var1)) {
            this.h.put(var1, Boolean.TRUE);
            return false;
        } else {
            return true;
        }
    }

    public static class a1 {
        private  e a;
        private  ArrayList<String> b;

        public a1(e var1, String var2) {
            com.iab.omid.library.vungle.walking.a.a1 var10000 = this;
            com.iab.omid.library.vungle.walking.a.a1 var10001 = this;
            com.iab.omid.library.vungle.walking.a.a1 var10002 = this;

            ArrayList var3;
            var3 = new ArrayList();
            var10002.b = var3;
            var10001.a = var1;
            var10000.a(var2);
        }

        public void a(String var1) {
            this.b.add(var1);
        }

        public e a() {
            return this.a;
        }

        public ArrayList<String> b() {
            return this.b;
        }
    }
}

