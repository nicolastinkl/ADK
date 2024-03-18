package com.iab.omid.library.bytedance2.utils;

import android.app.UiModeManager;
import android.content.Context;
import com.iab.omid.library.bytedance2.adsession.DeviceCategory;

/* renamed from: com.iab.omid.library.bytedance2.utils.a */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/utils/a.class */
public final class C1222a {

    /* renamed from: a */
    private static UiModeManager f6775a;

    /* renamed from: a */
    public static void m104a(Context context) {
        if (context != null) {
            f6775a = (UiModeManager) context.getSystemService("uimode");
        }
    }

    /* renamed from: a */
    public static DeviceCategory m105a() {
        int currentModeType = f6775a.getCurrentModeType();
        return currentModeType != 1 ? currentModeType != 4 ? DeviceCategory.OTHER : DeviceCategory.CTV : DeviceCategory.MOBILE;
    }
}
