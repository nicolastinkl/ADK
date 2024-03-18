package com.iab.omid.library.bytedance2.internal;

import android.content.Context;
import android.os.Handler;
import com.iab.omid.library.bytedance2.adsession.C1195a;
import com.iab.omid.library.bytedance2.devicevolume.C1199b;
import com.iab.omid.library.bytedance2.devicevolume.C1201d;
import com.iab.omid.library.bytedance2.devicevolume.C1202e;
import com.iab.omid.library.bytedance2.devicevolume.InterfaceC1200c;
import com.iab.omid.library.bytedance2.internal.C1206d;
import com.iab.omid.library.bytedance2.walking.TreeWalker;

/* renamed from: com.iab.omid.library.bytedance2.internal.h */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/internal/h.class */
public class C1212h implements InterfaceC1200c, C1206d.InterfaceC1207a {

    /* renamed from: f */
    private static C1212h f6750f;

    /* renamed from: a */
    private float f6751a = 0.0f;

    /* renamed from: b */
    private final C1202e f6752b;

    /* renamed from: c */
    private final C1199b f6753c;

    /* renamed from: d */
    private C1201d f6754d;

    /* renamed from: e */
    private C1205c f6755e;

    public C1212h(C1202e c1202e, C1199b c1199b) {
        this.f6752b = c1202e;
        this.f6753c = c1199b;
    }

    /* renamed from: c */
    public static C1212h m141c() {
        if (f6750f == null) {
            f6750f = new C1212h(new C1202e(), new C1199b());
        }
        return f6750f;
    }

    /* renamed from: a */
    private C1205c m146a() {
        if (this.f6755e == null) {
            this.f6755e = C1205c.m184c();
        }
        return this.f6755e;
    }

    /* renamed from: a */
    public void m144a(Context context) {
        this.f6754d = this.f6752b.m195a(new Handler(), context, this.f6753c.m201a(), this);
    }

    /* renamed from: d */
    public void m140d() {
        C1204b.m189g().m179a(this);
        C1204b.m189g().m173e();
        TreeWalker.getInstance().m38h();
        this.f6754d.m197c();
    }

    /* renamed from: e */
    public void m139e() {
        TreeWalker.getInstance().m36j();
        C1204b.m189g().m172f();
        this.f6754d.m196d();
    }

    @Override // com.iab.omid.library.bytedance2.internal.C1206d.InterfaceC1207a
    /* renamed from: a */
    public void mo143a(boolean z) {
        if (z) {
            TreeWalker.getInstance().m38h();
        } else {
            TreeWalker.getInstance().m39g();
        }
    }

    @Override // com.iab.omid.library.bytedance2.devicevolume.InterfaceC1200c
    /* renamed from: a */
    public void mo145a(float f) {
        this.f6751a = f;
        for (C1195a c1195a : m146a().m188a()) {
            c1195a.getAdSessionStatePublisher().m130a(f);
        }
    }

    /* renamed from: b */
    public float m142b() {
        return this.f6751a;
    }
}
