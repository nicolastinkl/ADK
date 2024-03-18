package com.iab.omid.library.bytedance2.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import com.iab.omid.library.bytedance2.adsession.DeviceCategory;
import com.iab.omid.library.bytedance2.adsession.OutputDeviceStatus;

/* renamed from: com.iab.omid.library.bytedance2.utils.e */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/utils/e.class */
public class C1228e {

    /* renamed from: a */
    private static OutputDeviceStatus f6782a = OutputDeviceStatus.UNKNOWN;

    /* renamed from: com.iab.omid.library.bytedance2.utils.e$a */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/utils/e$a.class */
    static class C1229a extends BroadcastReceiver {

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "android.media.action.HDMI_AUDIO_PLUG") {
                int intExtra = intent.getIntExtra("android.media.extra.AUDIO_PLUG_STATE", -1);
                if (intExtra == 0) {
                    OutputDeviceStatus unused = C1228e.f6782a = OutputDeviceStatus.NOT_DETECTED;
                } else if (intExtra == 1) {
                    OutputDeviceStatus unused2 = C1228e.f6782a = OutputDeviceStatus.UNKNOWN;
                }
            }
        }
    }

    /* renamed from: a */
    public static void m75a(@NonNull Context context) {
        context.registerReceiver(new C1229a(), new IntentFilter("android.media.action.HDMI_AUDIO_PLUG"));
    }

    /* renamed from: a */
    public static OutputDeviceStatus m76a() {
        return C1222a.m105a() != DeviceCategory.CTV ? OutputDeviceStatus.UNKNOWN : f6782a;
    }
}
