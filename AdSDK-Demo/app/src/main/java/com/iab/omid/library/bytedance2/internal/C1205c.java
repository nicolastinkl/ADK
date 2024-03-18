package com.iab.omid.library.bytedance2.internal;

import com.iab.omid.library.bytedance2.adsession.C1195a;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/* renamed from: com.iab.omid.library.bytedance2.internal.c */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/internal/c.class */
public class C1205c {

    /* renamed from: c */
    private static C1205c f6734c = new C1205c();

    /* renamed from: a */
    private final ArrayList<C1195a> f6735a = new ArrayList<>();

    /* renamed from: b */
    private final ArrayList<C1195a> f6736b = new ArrayList<>();

    private C1205c() {
    }

    /* renamed from: c */
    public static C1205c m184c() {
        return f6734c;
    }

    /* renamed from: a */
    public void m187a(C1195a c1195a) {
        this.f6735a.add(c1195a);
    }

    /* renamed from: c */
    public void m183c(C1195a c1195a) {
        boolean m182d = m182d();
        this.f6736b.add(c1195a);
        if (m182d) {
            return;
        }
        C1212h.m141c().m140d();
    }

    /* renamed from: b */
    public void m185b(C1195a c1195a) {
        boolean m182d = m182d();
        this.f6735a.remove(c1195a);
        this.f6736b.remove(c1195a);
        if (!m182d || m182d()) {
            return;
        }
        C1212h.m141c().m139e();
    }

    /* renamed from: b */
    public Collection<C1195a> m186b() {
        return Collections.unmodifiableCollection(this.f6735a);
    }

    /* renamed from: a */
    public Collection<C1195a> m188a() {
        return Collections.unmodifiableCollection(this.f6736b);
    }

    /* renamed from: d */
    public boolean m182d() {
        return this.f6736b.size() > 0;
    }
}
