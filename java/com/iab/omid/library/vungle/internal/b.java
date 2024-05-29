//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.internal;

import android.annotation.SuppressLint;
import android.view.View;
import com.iab.omid.library.vungle.adsession.a;
import java.util.Iterator;
import com.iab.omid.library.vungle.adsession.a;

public class b extends d {
    @SuppressLint({"StaticFieldLeak"})
    private static b d = new b();

    private b() {
    }

    public static b g() {
        return d;
    }

    public boolean d() {
        Iterator var2 = c.c().a().iterator();

        View var1;

        do {
            if (!var2.hasNext()) {
                return false;
            }
        } while((var1 = ((com.iab.omid.library.vungle.adsession.a)var2.next()).c()) == null || !var1.hasWindowFocus());

        return true;
    }

    public void b(boolean var1) {
        Iterator var2 = c.c().b().iterator();

        while(var2.hasNext()) {

            ((com.iab.omid.library.vungle.adsession.a)var2.next()).getAdSessionStatePublisher().a(var1);
        }

    }
}
