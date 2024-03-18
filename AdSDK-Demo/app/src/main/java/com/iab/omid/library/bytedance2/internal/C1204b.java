package com.iab.omid.library.bytedance2.internal;

import android.annotation.SuppressLint;
import android.view.View;
import com.iab.omid.library.bytedance2.adsession.C1195a;

/* renamed from: com.iab.omid.library.bytedance2.internal.b */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/internal/b.class */
public class C1204b extends C1206d {
    @SuppressLint({"StaticFieldLeak"})

    /* renamed from: d */
    private static C1204b f6733d = new C1204b();

    private C1204b() {
    }

    /* renamed from: g */
    public static C1204b m189g() {
        return f6733d;
    }

    @Override // com.iab.omid.library.bytedance2.internal.C1206d
    /* renamed from: d */
    public boolean mo174d() {
        for (C1195a c1195a : C1205c.m184c().m188a()) {
            View m227c = c1195a.m227c();
            if (m227c != null && m227c.hasWindowFocus()) {
                return true;
            }
        }
        return false;
    }

    @Override // com.iab.omid.library.bytedance2.internal.C1206d
    /* renamed from: b */
    public void mo176b(boolean z) {
        for (C1195a c1195a : C1205c.m184c().m186b()) {
            c1195a.getAdSessionStatePublisher().m118a(z);
        }
    }
}
