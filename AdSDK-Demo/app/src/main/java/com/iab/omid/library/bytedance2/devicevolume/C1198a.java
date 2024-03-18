package com.iab.omid.library.bytedance2.devicevolume;

/* renamed from: com.iab.omid.library.bytedance2.devicevolume.a */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/devicevolume/a.class */
public class C1198a {
    /* renamed from: a */
    public float m202a(int i, int i2) {
        if (i2 <= 0 || i <= 0) {
            return 0.0f;
        }
        float f = i / i2;
        float f2 = f;
        if (f > 1.0f) {
            f2 = 1.0f;
        }
        return f2;
    }
}
