//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.adsession.media;

public enum InteractionType {
    CLICK("click"),
    INVITATION_ACCEPTED("invitationAccept");

    private String interactionType;

    private InteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    @Override
    public String toString() {
        return this.interactionType;
    }
}

