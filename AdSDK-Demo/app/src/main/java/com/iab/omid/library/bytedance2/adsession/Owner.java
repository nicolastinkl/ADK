package com.iab.omid.library.bytedance2.adsession;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/Owner.class */
public enum Owner {
    NATIVE("native"),
    JAVASCRIPT("javascript"),
    NONE("none");
    
    private final String owner;

    Owner(String str) {
        this.owner = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.owner;
    }
}
