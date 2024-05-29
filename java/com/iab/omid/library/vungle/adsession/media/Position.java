package com.iab.omid.library.vungle.adsession.media;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



public enum Position {
    PREROLL("preroll"),
    MIDROLL("midroll"),
    POSTROLL("postroll"),
    STANDALONE("standalone");

    private final String position;

    private Position(String var3) {
        this.position = var3;
    }
    public String toString() {
        return this.position;
    }
}
