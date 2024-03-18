package com.iab.omid.library.bytedance2.processor;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import com.iab.omid.library.bytedance2.processor.InterfaceC1213a;
import com.iab.omid.library.bytedance2.utils.C1224c;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.processor.d */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/processor/d.class */
public class C1217d implements InterfaceC1213a {

    /* renamed from: a */
    private final int[] f6759a = new int[2];

    /* renamed from: a */
    private void m133a(ViewGroup viewGroup, JSONObject jSONObject, InterfaceC1213a.InterfaceC1214a interfaceC1214a, boolean z) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            interfaceC1214a.mo50a(viewGroup.getChildAt(i), this, jSONObject, z);
        }
    }

    /* renamed from: b */
    private void m132b(ViewGroup viewGroup, JSONObject jSONObject, InterfaceC1213a.InterfaceC1214a interfaceC1214a, boolean z) {
        HashMap hashMap = new HashMap();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            ArrayList arrayList = (ArrayList) hashMap.get(Float.valueOf(childAt.getZ()));
            ArrayList arrayList2 = arrayList;
            if (arrayList == null) {
                ArrayList arrayList3 = new ArrayList();
                hashMap.put(Float.valueOf(childAt.getZ()), arrayList2);
            }
            arrayList2.add(childAt);
        }
        ArrayList arrayList4 = new ArrayList(hashMap.keySet());
        Collections.sort(arrayList4);
        Iterator it = arrayList4.iterator();
        while (it.hasNext()) {
            Iterator it2 = ((ArrayList) hashMap.get((Float) it.next())).iterator();
            while (it2.hasNext()) {
                interfaceC1214a.mo50a((View) it2.next(), this, jSONObject, z);
            }
        }
    }

    @Override // com.iab.omid.library.bytedance2.processor.InterfaceC1213a
    /* renamed from: a */
    public JSONObject mo135a(View view) {
        if (view == null) {
            return C1224c.m98a(0, 0, 0, 0);
        }
        int width = view.getWidth();
        int height = view.getHeight();
        view.getLocationOnScreen(this.f6759a);
        int[] iArr = this.f6759a;
        return C1224c.m98a(iArr[0], iArr[1], width, height);
    }

    @Override // com.iab.omid.library.bytedance2.processor.InterfaceC1213a
    /* renamed from: a */
    public void mo134a(View view, JSONObject jSONObject, InterfaceC1213a.InterfaceC1214a interfaceC1214a, boolean z, boolean z2) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (!z || Build.VERSION.SDK_INT < 21) {
                m133a(viewGroup, jSONObject, interfaceC1214a, z2);
            } else {
                m132b(viewGroup, jSONObject, interfaceC1214a, z2);
            }
        }
    }
}
