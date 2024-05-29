//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import com.iab.omid.library.vungle.adsession.DeviceCategory;
import com.iab.omid.library.vungle.adsession.OutputDeviceStatus;

public class e {
    private static OutputDeviceStatus a;

    public static void a(@NonNull Context var0) {
        Context var10000 = var0;
        IntentFilter var2;
        var2 = new IntentFilter("android.media.action.HDMI_AUDIO_PLUG");
        BroadcastReceiver var1;
        var1 = new BroadcastReceiver() {
            public void onReceive(Context var1, Intent var2) {
                if (var2.getAction() == "android.media.action.HDMI_AUDIO_PLUG") {
                    int var3;
                    if ((var3 = var2.getIntExtra("android.media.extra.AUDIO_PLUG_STATE", -1)) == 0) {
                        e.a = OutputDeviceStatus.NOT_DETECTED;
                    } else if (var3 == 1) {
                        e.a = OutputDeviceStatus.UNKNOWN;
                    }
                }

            }
        };
        var10000.registerReceiver(var1, var2);
    }

    public static OutputDeviceStatus a() {
        return com.iab.omid.library.vungle.utils.a.a() != DeviceCategory.CTV ? OutputDeviceStatus.UNKNOWN : a;
    }

    static {
        a = OutputDeviceStatus.UNKNOWN;
    }
}
