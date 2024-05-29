//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.utils;

import android.app.UiModeManager;
import android.content.Context;
import com.iab.omid.library.vungle.adsession.DeviceCategory;

public final class a {
    private static UiModeManager a;

    public static void a(Context var0) {
        if (var0 != null) {
            a = (UiModeManager)var0.getSystemService(Context.UI_MODE_SERVICE);
        }

    }

    public static DeviceCategory a() {
        UiModeManager var0;
        if ((var0 = a) != null) {
            int var1;
            if ((var1 = var0.getCurrentModeType()) != 1) {
                return var1 != 4 ? DeviceCategory.OTHER : DeviceCategory.CTV;
            } else {
                return DeviceCategory.MOBILE;
            }
        } else {
            return DeviceCategory.OTHER;
        }
    }
}
