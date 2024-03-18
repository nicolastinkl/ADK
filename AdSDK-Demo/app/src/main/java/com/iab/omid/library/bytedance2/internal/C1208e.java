package com.iab.omid.library.bytedance2.internal;

import android.view.View;
import androidx.annotation.Nullable;
import com.iab.omid.library.bytedance2.adsession.FriendlyObstructionPurpose;
import com.iab.omid.library.bytedance2.weakreference.C1248a;

/* renamed from: com.iab.omid.library.bytedance2.internal.e */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/internal/e.class */
public class C1208e {

    /* renamed from: a */
    private final C1248a f6740a;

    /* renamed from: b */
    private final String f6741b;

    /* renamed from: c */
    private final FriendlyObstructionPurpose f6742c;

    /* renamed from: d */
    private final String f6743d;

    public C1208e(View view, FriendlyObstructionPurpose friendlyObstructionPurpose, @Nullable String str) {
        this.f6740a = new C1248a(view);
        this.f6741b = view.getClass().getCanonicalName();
        this.f6742c = friendlyObstructionPurpose;
        this.f6743d = str;
    }

    /* renamed from: c */
    public C1248a m169c() {
        return this.f6740a;
    }

    /* renamed from: d */
    public String m168d() {
        return this.f6741b;
    }

    /* renamed from: b */
    public FriendlyObstructionPurpose m170b() {
        return this.f6742c;
    }

    /* renamed from: a */
    public String m171a() {
        return this.f6743d;
    }
}
