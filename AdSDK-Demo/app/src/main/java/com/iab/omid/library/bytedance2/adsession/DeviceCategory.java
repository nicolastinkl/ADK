package com.iab.omid.library.bytedance2.adsession;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/DeviceCategory.class */
public enum DeviceCategory {
    CTV("ctv"),
    MOBILE("mobile"),
    OTHER("other");
    
    private final String deviceCategory;

    DeviceCategory(String str) {
        this.deviceCategory = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.deviceCategory;
    }
}
