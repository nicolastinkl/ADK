package com.iab.omid.library.bytedance2.internal;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

/* renamed from: com.iab.omid.library.bytedance2.internal.d */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/internal/d.class */
public class C1206d implements Application.ActivityLifecycleCallbacks {

    /* renamed from: a */
    private boolean f6737a;

    /* renamed from: b */
    protected boolean f6738b;

    /* renamed from: c */
    private InterfaceC1207a f6739c;

    /* renamed from: com.iab.omid.library.bytedance2.internal.d$a */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/internal/d$a.class */
    public interface InterfaceC1207a {
        /* renamed from: a */
        void mo143a(boolean z);
    }

    /* renamed from: a */
    private boolean m181a() {
        return (m177b().importance == 100) || mo174d();
    }

    /* renamed from: a */
    private void m178a(boolean z) {
        if (this.f6738b != z) {
            this.f6738b = z;
            if (this.f6737a) {
                mo176b(z);
                InterfaceC1207a interfaceC1207a = this.f6739c;
                if (interfaceC1207a != null) {
                    interfaceC1207a.mo143a(z);
                }
            }
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
        m178a(true);
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
        m178a(m181a());
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
    }

    /* renamed from: a */
    public void m180a(@NonNull Context context) {
        if (context instanceof Application) {
            ((Application) context).registerActivityLifecycleCallbacks(this);
        }
    }

    /* renamed from: e */
    public void m173e() {
        this.f6737a = true;
        boolean m181a = m181a();
        this.f6738b = m181a;
        mo176b(m181a);
    }

    /* renamed from: c */
    public boolean m175c() {
        return this.f6738b;
    }

    /* renamed from: f */
    public void m172f() {
        this.f6737a = false;
        this.f6739c = null;
    }

    /* renamed from: a */
    public void m179a(InterfaceC1207a interfaceC1207a) {
        this.f6739c = interfaceC1207a;
    }

    /* renamed from: d */
    protected boolean mo174d() {
        return false;
    }

    /* renamed from: b */
    protected void mo176b(boolean z) {
    }

    @VisibleForTesting
    /* renamed from: b */
    ActivityManager.RunningAppProcessInfo m177b() {
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        return runningAppProcessInfo;
    }
}
