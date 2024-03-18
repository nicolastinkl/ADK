package com.iab.omid.library.bytedance2.walking;

import androidx.annotation.VisibleForTesting;
import com.iab.omid.library.bytedance2.walking.async.AbstractAsyncTaskC1239b;
import com.iab.omid.library.bytedance2.walking.async.AsyncTaskC1243d;
import com.iab.omid.library.bytedance2.walking.async.AsyncTaskC1244e;
import com.iab.omid.library.bytedance2.walking.async.AsyncTaskC1245f;
import com.iab.omid.library.bytedance2.walking.async.C1242c;
import java.util.HashSet;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.walking.b */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/b.class */
public class C1246b implements AbstractAsyncTaskC1239b.InterfaceC1241b {

    /* renamed from: a */
    private JSONObject f6817a;

    /* renamed from: b */
    private final C1242c f6818b;

    public C1246b(C1242c c1242c) {
        this.f6818b = c1242c;
    }

    /* renamed from: b */
    public void m0b(JSONObject jSONObject, HashSet<String> hashSet, long j) {
        this.f6818b.m11b(new AsyncTaskC1245f(this, hashSet, jSONObject, j));
    }

    /* renamed from: a */
    public void m2a(JSONObject jSONObject, HashSet<String> hashSet, long j) {
        this.f6818b.m11b(new AsyncTaskC1244e(this, hashSet, jSONObject, j));
    }

    /* renamed from: b */
    public void m1b() {
        this.f6818b.m11b(new AsyncTaskC1243d(this));
    }

    @Override // com.iab.omid.library.bytedance2.walking.async.AbstractAsyncTaskC1239b.InterfaceC1241b
    @VisibleForTesting
    /* renamed from: a */
    public JSONObject mo4a() {
        return this.f6817a;
    }

    @Override // com.iab.omid.library.bytedance2.walking.async.AbstractAsyncTaskC1239b.InterfaceC1241b
    @VisibleForTesting
    /* renamed from: a */
    public void mo3a(JSONObject jSONObject) {
        this.f6817a = jSONObject;
    }
}
