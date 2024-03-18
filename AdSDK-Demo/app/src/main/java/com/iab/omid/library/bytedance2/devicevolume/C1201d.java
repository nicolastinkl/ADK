package com.iab.omid.library.bytedance2.devicevolume;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings;

/* renamed from: com.iab.omid.library.bytedance2.devicevolume.d */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/devicevolume/d.class */
public final class C1201d extends ContentObserver {

    /* renamed from: a */
    private final Context f6722a;

    /* renamed from: b */
    private final AudioManager f6723b;

    /* renamed from: c */
    private final C1198a f6724c;

    /* renamed from: d */
    private final InterfaceC1200c f6725d;

    /* renamed from: e */
    private float f6726e;

    public C1201d(Handler handler, Context context, C1198a c1198a, InterfaceC1200c interfaceC1200c) {
        super(handler);
        this.f6722a = context;
        this.f6723b = (AudioManager) context.getSystemService("audio");
        this.f6724c = c1198a;
        this.f6725d = interfaceC1200c;
    }

    /* renamed from: a */
    private float m200a() {
        return this.f6724c.m202a(this.f6723b.getStreamVolume(3), this.f6723b.getStreamMaxVolume(3));
    }

    /* renamed from: a */
    private boolean m199a(float f) {
        return f != this.f6726e;
    }

    /* renamed from: b */
    private void m198b() {
        this.f6725d.mo145a(this.f6726e);
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z) {
        super.onChange(z);
        float m200a = m200a();
        if (m199a(m200a)) {
            this.f6726e = m200a;
            m198b();
        }
    }

    /* renamed from: c */
    public void m197c() {
        this.f6726e = m200a();
        m198b();
        this.f6722a.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, this);
    }

    /* renamed from: d */
    public void m196d() {
        this.f6722a.getContentResolver().unregisterContentObserver(this);
    }
}
