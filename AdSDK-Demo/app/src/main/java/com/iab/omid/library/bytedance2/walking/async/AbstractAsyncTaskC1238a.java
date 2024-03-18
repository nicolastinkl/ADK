package com.iab.omid.library.bytedance2.walking.async;

import com.iab.omid.library.bytedance2.walking.async.AbstractAsyncTaskC1239b;
import java.util.HashSet;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.walking.async.a */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/async/a.class */
public abstract class AbstractAsyncTaskC1238a extends AbstractAsyncTaskC1239b {

    /* renamed from: c */
    protected final HashSet<String> f6808c;

    /* renamed from: d */
    protected final JSONObject f6809d;

    /* renamed from: e */
    protected final long f6810e;

    public AbstractAsyncTaskC1238a(AbstractAsyncTaskC1239b.InterfaceC1241b interfaceC1241b, HashSet<String> hashSet, JSONObject jSONObject, long j) {
        super(interfaceC1241b);
        this.f6808c = new HashSet<>(hashSet);
        this.f6809d = jSONObject;
        this.f6810e = j;
    }
}
