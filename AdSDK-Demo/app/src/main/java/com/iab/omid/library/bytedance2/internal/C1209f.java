package com.iab.omid.library.bytedance2.internal;

import android.annotation.SuppressLint;
import android.content.Context;

/* renamed from: com.iab.omid.library.bytedance2.internal.f */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/internal/f.class */
public class C1209f {
    @SuppressLint({"StaticFieldLeak"})

    /* renamed from: b */
    private static C1209f f6744b = new C1209f();

    /* renamed from: a */
    private Context f6745a;

    /* renamed from: b */
    public static C1209f m165b() {
        return f6744b;
    }

    private C1209f() {
    }

    /* renamed from: a */
    public Context m167a() {
        return this.f6745a;
    }

    /* renamed from: a */
    public void m166a(Context context) {
        this.f6745a = context != null ? context.getApplicationContext() : null;
    }
}
