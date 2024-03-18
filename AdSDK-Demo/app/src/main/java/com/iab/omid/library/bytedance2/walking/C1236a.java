package com.iab.omid.library.bytedance2.walking;

import android.os.Build;
import android.view.View;
import android.view.ViewParent;
import com.iab.omid.library.bytedance2.adsession.C1195a;
import com.iab.omid.library.bytedance2.internal.C1205c;
import com.iab.omid.library.bytedance2.internal.C1208e;
import com.iab.omid.library.bytedance2.utils.C1232h;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;

/* renamed from: com.iab.omid.library.bytedance2.walking.a */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/a.class */
public class C1236a {

    /* renamed from: a */
    private final HashMap<View, String> f6797a = new HashMap<>();

    /* renamed from: b */
    private final HashMap<View, C1237a> f6798b = new HashMap<>();

    /* renamed from: c */
    private final HashMap<String, View> f6799c = new HashMap<>();

    /* renamed from: d */
    private final HashSet<View> f6800d = new HashSet<>();

    /* renamed from: e */
    private final HashSet<String> f6801e = new HashSet<>();

    /* renamed from: f */
    private final HashSet<String> f6802f = new HashSet<>();

    /* renamed from: g */
    private final HashMap<String, String> f6803g = new HashMap<>();

    /* renamed from: h */
    private final Map<View, Boolean> f6804h = new WeakHashMap();

    /* renamed from: i */
    private boolean f6805i;

    /* renamed from: com.iab.omid.library.bytedance2.walking.a$a */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/a$a.class */
    public static class C1237a {

        /* renamed from: a */
        private final C1208e f6806a;

        /* renamed from: b */
        private final ArrayList<String> f6807b = new ArrayList<>();

        public C1237a(C1208e c1208e, String str) {
            this.f6806a = c1208e;
            m17a(str);
        }

        /* renamed from: a */
        public void m17a(String str) {
            this.f6807b.add(str);
        }

        /* renamed from: a */
        public C1208e m18a() {
            return this.f6806a;
        }

        /* renamed from: b */
        public ArrayList<String> m16b() {
            return this.f6807b;
        }
    }

    /* renamed from: a */
    private String m32a(View view) {
        if (Build.VERSION.SDK_INT < 19 || view.isAttachedToWindow()) {
            if (m27b(view).booleanValue()) {
                return "noWindowFocus";
            }
            HashSet hashSet = new HashSet();
            while (view != null) {
                String m58a = C1232h.m58a(view);
                if (m58a != null) {
                    return m58a;
                }
                hashSet.add(view);
                ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
            this.f6800d.addAll(hashSet);
            return null;
        }
        return "notAttached";
    }

    /* renamed from: b */
    private Boolean m27b(View view) {
        if (view.hasWindowFocus()) {
            this.f6804h.remove(view);
            return Boolean.FALSE;
        } else if (this.f6804h.containsKey(view)) {
            return this.f6804h.get(view);
        } else {
            Map<View, Boolean> map = this.f6804h;
            Boolean bool = Boolean.FALSE;
            map.put(view, bool);
            return bool;
        }
    }

    /* renamed from: a */
    private void m31a(C1195a c1195a) {
        for (C1208e c1208e : c1195a.m225d()) {
            m30a(c1208e, c1195a);
        }
    }

    /* renamed from: a */
    private void m30a(C1208e c1208e, C1195a c1195a) {
        View view = c1208e.m169c().get();
        if (view == null) {
            return;
        }
        C1237a c1237a = this.f6798b.get(view);
        if (c1237a != null) {
            c1237a.m17a(c1195a.getAdSessionId());
        } else {
            this.f6798b.put(view, new C1237a(c1208e, c1195a.getAdSessionId()));
        }
    }

    /* renamed from: c */
    public HashSet<String> m25c() {
        return this.f6801e;
    }

    /* renamed from: b */
    public HashSet<String> m28b() {
        return this.f6802f;
    }

    /* renamed from: b */
    public String m26b(String str) {
        return this.f6803g.get(str);
    }

    /* renamed from: e */
    public void m21e() {
        C1205c m184c = C1205c.m184c();
        if (m184c != null) {
            for (C1195a c1195a : m184c.m188a()) {
                View m227c = c1195a.m227c();
                if (c1195a.m222f()) {
                    String adSessionId = c1195a.getAdSessionId();
                    if (m227c != null) {
                        String m32a = m32a(m227c);
                        if (m32a == null) {
                            this.f6801e.add(adSessionId);
                            this.f6797a.put(m227c, adSessionId);
                            m31a(c1195a);
                        } else if (m32a != "noWindowFocus") {
                            this.f6802f.add(adSessionId);
                            this.f6799c.put(adSessionId, m227c);
                            this.f6803g.put(adSessionId, m32a);
                        }
                    } else {
                        this.f6802f.add(adSessionId);
                        this.f6803g.put(adSessionId, "noAdView");
                    }
                }
            }
        }
    }

    /* renamed from: a */
    public void m33a() {
        this.f6797a.clear();
        this.f6798b.clear();
        this.f6799c.clear();
        this.f6800d.clear();
        this.f6801e.clear();
        this.f6802f.clear();
        this.f6803g.clear();
        this.f6805i = false;
    }

    /* renamed from: d */
    public void m23d() {
        this.f6805i = true;
    }

    /* renamed from: d */
    public String m22d(View view) {
        if (this.f6797a.size() == 0) {
            return null;
        }
        String str = this.f6797a.get(view);
        if (str != null) {
            this.f6797a.remove(view);
        }
        return str;
    }

    /* renamed from: a */
    public View m29a(String str) {
        return this.f6799c.get(str);
    }

    /* renamed from: c */
    public C1237a m24c(View view) {
        C1237a c1237a = this.f6798b.get(view);
        if (c1237a != null) {
            this.f6798b.remove(view);
        }
        return c1237a;
    }

    /* renamed from: e */
    public EnumC1247c m20e(View view) {
        return this.f6800d.contains(view) ? EnumC1247c.PARENT_VIEW : this.f6805i ? EnumC1247c.OBSTRUCTION_VIEW : EnumC1247c.UNDERLYING_VIEW;
    }

    /* renamed from: f */
    public boolean m19f(View view) {
        if (this.f6804h.containsKey(view)) {
            this.f6804h.put(view, Boolean.TRUE);
            return false;
        }
        return true;
    }
}
