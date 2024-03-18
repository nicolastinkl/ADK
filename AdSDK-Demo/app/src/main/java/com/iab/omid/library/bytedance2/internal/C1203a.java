package com.iab.omid.library.bytedance2.internal;

import android.content.Context;
import androidx.annotation.NonNull;
import com.iab.omid.library.bytedance2.adsession.C1195a;
import com.iab.omid.library.bytedance2.internal.C1206d;
import com.iab.omid.library.bytedance2.utils.C1230f;
import java.util.Date;

/* renamed from: com.iab.omid.library.bytedance2.internal.a */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/internal/a.class */
public class C1203a implements C1206d.InterfaceC1207a {

    /* renamed from: f */
    private static C1203a f6727f = new C1203a(new C1206d());

    /* renamed from: a */
    protected C1230f f6728a = new C1230f();

    /* renamed from: b */
    private Date f6729b;

    /* renamed from: c */
    private boolean f6730c;

    /* renamed from: d */
    private C1206d f6731d;

    /* renamed from: e */
    private boolean f6732e;

    private C1203a(C1206d c1206d) {
        this.f6731d = c1206d;
    }

    /* renamed from: a */
    public static C1203a m194a() {
        return f6727f;
    }

    /* renamed from: c */
    private void m191c() {
        if (!this.f6730c || this.f6729b == null) {
            return;
        }
        for (C1195a c1195a : C1205c.m184c().m188a()) {
            c1195a.getAdSessionStatePublisher().m120a(m192b());
        }
    }

    /* renamed from: b */
    public Date m192b() {
        Date date = this.f6729b;
        if (date != null) {
            return (Date) date.clone();
        }
        return null;
    }

    /* renamed from: a */
    public void m193a(@NonNull Context context) {
        if (this.f6730c) {
            return;
        }
        this.f6731d.m180a(context);
        this.f6731d.m179a(this);
        this.f6731d.m173e();
        this.f6732e = this.f6731d.m175c();
        this.f6730c = true;
    }

    @Override // com.iab.omid.library.bytedance2.internal.C1206d.InterfaceC1207a
    /* renamed from: a */
    public void mo143a(boolean z) {
        if (!this.f6732e && z) {
            m190d();
        }
        this.f6732e = z;
    }

    /* renamed from: d */
    public void m190d() {
        Date m73a = this.f6728a.m73a();
        Date date = this.f6729b;
        if (date == null || m73a.after(date)) {
            this.f6729b = m73a;
            m191c();
        }
    }
}
