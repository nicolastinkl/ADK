//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.adsession;

import androidx.annotation.NonNull;
import com.iab.omid.library.vungle.adsession.media.VastProperties;
import com.iab.omid.library.vungle.utils.g;

public final class AdEvents {
    private final a adSession;

    private AdEvents(a var1) {
        this.adSession = var1;
    }

    public static AdEvents createAdEvents(AdSession var0) {
        a var10000 = (a)var0;
        a var1;
        a var10001 = var1 = (a)var0;
        g.a(var0, "AdSession is null");
        g.g(var10001);
        g.b(var10000);
        AdEvents var2;
        AdEvents var3 = var2 = new AdEvents(var1);
        var1.getAdSessionStatePublisher().a(var2);
        return var3;
    }

    public void impressionOccurred() {
        g.b(this.adSession);
        g.e(this.adSession);
        if (!this.adSession.f()) {
            try {
                this.adSession.start();
            } catch (Exception var1) {
            }
        }

        if (this.adSession.f()) {
            this.adSession.k();
        }

    }

    public void loaded() {
        g.a(this.adSession);
        g.e(this.adSession);
        this.adSession.l();
    }

    public void loaded(@NonNull VastProperties var1) {
        g.a(var1, "VastProperties is null");
        g.a(this.adSession);
        g.e(this.adSession);
        this.adSession.a(var1.a());
    }
}
