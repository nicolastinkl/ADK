package com.iab.omid.library.bytedance2.walking.async;

import android.os.AsyncTask;
import java.util.concurrent.ThreadPoolExecutor;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.walking.async.b */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/async/b.class */
public abstract class AbstractAsyncTaskC1239b extends AsyncTask<Object, Void, String> {

    /* renamed from: a */
    private InterfaceC1240a f6811a;

    /* renamed from: b */
    protected final InterfaceC1241b f6812b;

    /* renamed from: com.iab.omid.library.bytedance2.walking.async.b$a */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/async/b$a.class */
    public interface InterfaceC1240a {
        /* renamed from: a */
        void mo12a(AbstractAsyncTaskC1239b abstractAsyncTaskC1239b);
    }

    /* renamed from: com.iab.omid.library.bytedance2.walking.async.b$b */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/async/b$b.class */
    public interface InterfaceC1241b {
        /* renamed from: a */
        JSONObject mo4a();

        /* renamed from: a */
        void mo3a(JSONObject jSONObject);
    }

    public AbstractAsyncTaskC1239b(InterfaceC1241b interfaceC1241b) {
        this.f6812b = interfaceC1241b;
    }

    /* renamed from: a */
    public void m15a(InterfaceC1240a interfaceC1240a) {
        this.f6811a = interfaceC1240a;
    }

    /* renamed from: a */
    public void m14a(ThreadPoolExecutor threadPoolExecutor) {
        executeOnExecutor(threadPoolExecutor, new Object[0]);
    }

    /* JADX DEBUG: Method merged with bridge method */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    /* renamed from: a */
    public void onPostExecute(String str) {
        InterfaceC1240a interfaceC1240a = this.f6811a;
        if (interfaceC1240a != null) {
            interfaceC1240a.mo12a(this);
        }
    }
}
