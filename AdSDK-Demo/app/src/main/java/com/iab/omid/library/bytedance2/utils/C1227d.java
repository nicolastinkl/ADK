package com.iab.omid.library.bytedance2.utils;

import android.text.TextUtils;
import android.util.Log;
import com.iab.omid.library.bytedance2.C1194a;

/* renamed from: com.iab.omid.library.bytedance2.utils.d */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/utils/d.class */
public final class C1227d {
    /* renamed from: a */
    public static void m78a(String str) {
        if (!C1194a.f6697a.booleanValue() || TextUtils.isEmpty(str)) {
            return;
        }
        Log.i("OMIDLIB", str);
    }

    /* renamed from: a */
    public static void m77a(String str, Exception exc) {
        if ((!C1194a.f6697a.booleanValue() || TextUtils.isEmpty(str)) && exc == null) {
            return;
        }
        Log.e("OMIDLIB", str, exc);
    }
}
