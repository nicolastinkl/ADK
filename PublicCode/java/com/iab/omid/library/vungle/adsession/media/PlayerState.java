package com.iab.omid.library.vungle.adsession.media;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



public enum PlayerState {
    MINIMIZED("minimized"),
    COLLAPSED("collapsed"),
    NORMAL("normal"),
    EXPANDED("expanded"),
    FULLSCREEN("fullscreen");

    private final String playerState;

    private PlayerState(String var3) {
        this.playerState = var3;
    }

    public String toString() {
        return this.playerState;
    }
}

