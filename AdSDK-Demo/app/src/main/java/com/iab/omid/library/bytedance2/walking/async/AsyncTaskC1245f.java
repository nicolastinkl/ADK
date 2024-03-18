package com.iab.omid.library.bytedance2.walking.async;

import android.text.TextUtils;
import com.iab.omid.library.bytedance2.adsession.C1195a;
import com.iab.omid.library.bytedance2.internal.C1205c;
import com.iab.omid.library.bytedance2.utils.C1224c;
import com.iab.omid.library.bytedance2.walking.async.AbstractAsyncTaskC1239b;
import java.util.HashSet;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.walking.async.f */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/async/f.class */
public class AsyncTaskC1245f extends AbstractAsyncTaskC1238a {
    public AsyncTaskC1245f(AbstractAsyncTaskC1239b.InterfaceC1241b interfaceC1241b, HashSet<String> hashSet, JSONObject jSONObject, long j) {
        super(interfaceC1241b, hashSet, jSONObject, j);
    }

    /* renamed from: b */
    private void m5b(String str) {
        C1205c m184c = C1205c.m184c();
        if (m184c != null) {
            for (C1195a c1195a : m184c.m186b()) {
                if (this.f6808c.contains(c1195a.getAdSessionId())) {
                    c1195a.getAdSessionStatePublisher().m117b(str, this.f6810e);
                }
            }
        }
    }

    /* JADX DEBUG: Method merged with bridge method */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    /* renamed from: a */
    public String doInBackground(Object... objArr) {
        if (C1224c.m79h(this.f6809d, this.f6812b.mo4a())) {
            return null;
        }
        this.f6812b.mo3a(this.f6809d);
        return this.f6809d.toString();
    }

    /* JADX DEBUG: Method merged with bridge method */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.iab.omid.library.bytedance2.walking.async.AbstractAsyncTaskC1239b, android.os.AsyncTask
    /* renamed from: a */
    public void onPostExecute(String str) {
        if (!TextUtils.isEmpty(str)) {
            m5b(str);
        }
        super.onPostExecute(str);
    }
}
