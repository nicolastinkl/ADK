package com.iab.omid.library.bytedance2;

import android.content.Context;
import com.iab.omid.library.bytedance2.internal.C1203a;
import com.iab.omid.library.bytedance2.internal.C1204b;
import com.iab.omid.library.bytedance2.internal.C1209f;
import com.iab.omid.library.bytedance2.internal.C1212h;
import com.iab.omid.library.bytedance2.utils.C1222a;
import com.iab.omid.library.bytedance2.utils.C1224c;
import com.iab.omid.library.bytedance2.utils.C1228e;
import com.iab.omid.library.bytedance2.utils.C1231g;

/* renamed from: com.iab.omid.library.bytedance2.b */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/b.class */
public class C1196b {

    /* renamed from: a */
    private boolean f6714a;

    /* renamed from: b */
    private void m210b(Context context) {
        C1231g.m68a(context, "Application Context cannot be null");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public String m214a() {
        return "1.4.4-Bytedance2";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public boolean m211b() {
        return this.f6714a;
    }

    /* renamed from: a */
    void m212a(boolean z) {
        this.f6714a = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m213a(Context context) {
        m210b(context);
        if (m211b()) {
            return;
        }
        m212a(true);
        C1212h.m141c().m144a(context);
        C1204b.m189g().m180a(context);
        C1222a.m104a(context);
        C1224c.m97a(context);
        C1228e.m75a(context);
        C1209f.m165b().m166a(context);
        C1203a.m194a().m193a(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: c */
    public void m209c() {
        C1231g.m71a();
        C1203a.m194a().m190d();
    }
}
