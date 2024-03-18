//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.internal;

import android.view.View;
import androidx.annotation.Nullable;
import com.iab.omid.library.vungle.adsession.FriendlyObstructionPurpose;
import com.iab.omid.library.vungle.weakreference.a;

public class e {
    private final a a;
    private final String b;
    private final FriendlyObstructionPurpose c;
    private final String d;

    public e(View var1, FriendlyObstructionPurpose var2, @Nullable String var3) {
        this.a = new a(var1);
        this.b = var1.getClass().getCanonicalName();
        this.c = var2;
        this.d = var3;
    }

    public a c() {
        return this.a;
    }

    public String d() {
        return this.b;
    }

    public FriendlyObstructionPurpose b() {
        return this.c;
    }

    public String a() {
        return this.d;
    }
}
