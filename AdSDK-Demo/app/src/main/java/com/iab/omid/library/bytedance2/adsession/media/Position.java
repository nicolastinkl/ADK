package com.iab.omid.library.bytedance2.adsession.media;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/media/Position.class */
public enum Position {
    PREROLL("preroll"),
    MIDROLL("midroll"),
    POSTROLL("postroll"),
    STANDALONE("standalone");
    
    private final String position;

    Position(String str) {
        this.position = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.position;
    }
}
