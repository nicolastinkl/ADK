package com.iab.omid.library.bytedance2.adsession;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/ErrorType.class */
public enum ErrorType {
    GENERIC("generic"),
    VIDEO("video");
    
    private final String errorType;

    ErrorType(String str) {
        this.errorType = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.errorType;
    }
}
