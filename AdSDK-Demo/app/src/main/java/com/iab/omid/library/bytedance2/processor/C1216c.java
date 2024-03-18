package com.iab.omid.library.bytedance2.processor;

import android.view.View;
import androidx.annotation.NonNull;
import com.iab.omid.library.bytedance2.adsession.C1195a;
import com.iab.omid.library.bytedance2.internal.C1205c;
import com.iab.omid.library.bytedance2.processor.InterfaceC1213a;
import com.iab.omid.library.bytedance2.utils.C1224c;
import com.iab.omid.library.bytedance2.utils.C1228e;
import com.iab.omid.library.bytedance2.utils.C1232h;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.processor.c */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/processor/c.class */
public class C1216c implements InterfaceC1213a {

    /* renamed from: a */
    private final InterfaceC1213a f6758a;

    public C1216c(InterfaceC1213a interfaceC1213a) {
        this.f6758a = interfaceC1213a;
    }

    @Override // com.iab.omid.library.bytedance2.processor.InterfaceC1213a
    /* renamed from: a */
    public JSONObject mo135a(View view) {
        JSONObject m98a = C1224c.m98a(0, 0, 0, 0);
        C1224c.m93a(m98a, C1228e.m76a());
        return m98a;
    }

    @Override // com.iab.omid.library.bytedance2.processor.InterfaceC1213a
    /* renamed from: a */
    public void mo134a(View view, JSONObject jSONObject, InterfaceC1213a.InterfaceC1214a interfaceC1214a, boolean z, boolean z2) {
        Iterator<View> it = m136a().iterator();
        while (it.hasNext()) {
            interfaceC1214a.mo50a(it.next(), this.f6758a, jSONObject, z2);
        }
    }

    @NonNull
    /* renamed from: a */
    ArrayList<View> m136a() {
        View rootView;
        ArrayList<View> arrayList = new ArrayList<>();
        C1205c m184c = C1205c.m184c();
        if (m184c != null) {
            Collection<C1195a> m188a = m184c.m188a();
            IdentityHashMap identityHashMap = new IdentityHashMap((m188a.size() * 2) + 3);
            for (C1195a c1195a : m188a) {
                View m227c = c1195a.m227c();
                if (m227c != null && C1232h.m54e(m227c) && (rootView = m227c.getRootView()) != null && !identityHashMap.containsKey(rootView)) {
                    identityHashMap.put(rootView, rootView);
                    float m56c = C1232h.m56c(rootView);
                    int size = arrayList.size();
                    while (size > 0 && C1232h.m56c(arrayList.get(size - 1)) > m56c) {
                        size--;
                    }
                    arrayList.add(size, rootView);
                }
            }
        }
        return arrayList;
    }
}
