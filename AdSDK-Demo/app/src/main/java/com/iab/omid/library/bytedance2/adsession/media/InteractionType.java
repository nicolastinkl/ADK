package com.iab.omid.library.bytedance2.adsession.media;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/media/InteractionType.class */
public enum InteractionType {
    CLICK("click"),
    INVITATION_ACCEPTED("invitationAccept");
    
    String interactionType;

    InteractionType(String str) {
        this.interactionType = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.interactionType;
    }
}
