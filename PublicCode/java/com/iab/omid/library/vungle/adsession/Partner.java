//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.adsession;

import com.iab.omid.library.vungle.utils.g;

public class Partner {
    private final String name;
    private final String version;

    private Partner(String var1, String var2) {
        this.name = var1;
        this.version = var2;
    }

    public static Partner createPartner(String var0, String var1) {
        g.a(var0, "Name is null or empty");
        g.a(var1, "Version is null or empty");
        return new Partner(var0, var1);
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }
}
