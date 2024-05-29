//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.devicevolume;

public class a {
    public a() {
    }

    public float a(int var1, int var2) {
        if (var2 > 0 && var1 > 0) {
            float var3;
            if ((var3 = (float)var1 / (float)var2) > 1.0F) {
                var3 = 1.0F;
            }

            return var3;
        } else {
            return 0.0F;
        }
    }
}
