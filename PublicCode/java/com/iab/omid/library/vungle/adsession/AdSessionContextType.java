//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.adsession;
public enum AdSessionContextType {
    HTML("html"),
    NATIVE("native"),
    JAVASCRIPT("javascript"),
    HTML_JS("javascript");

    private final String typeString;

    private AdSessionContextType(String typeString) {
        this.typeString = typeString;
    }

    public String toString() {
        return this.typeString;
    }
}
