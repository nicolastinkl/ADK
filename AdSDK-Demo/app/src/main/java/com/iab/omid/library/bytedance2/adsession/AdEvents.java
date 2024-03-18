package com.iab.omid.library.bytedance2.adsession;

import androidx.annotation.NonNull;
import com.iab.omid.library.bytedance2.adsession.media.VastProperties;
import com.iab.omid.library.bytedance2.utils.C1231g;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/AdEvents.class */
public final class AdEvents {
    private final C1195a adSession;

    private AdEvents(C1195a c1195a) {
        this.adSession = c1195a;
    }

    public static AdEvents createAdEvents(AdSession adSession) {
        C1195a c1195a = (C1195a) adSession;
        C1231g.m68a(adSession, "AdSession is null");
        C1231g.m60g(c1195a);
        C1231g.m65b(c1195a);
        AdEvents adEvents = new AdEvents(c1195a);
        c1195a.getAdSessionStatePublisher().m128a(adEvents);
        return adEvents;
    }

    public void impressionOccurred() {
        C1231g.m65b(this.adSession);
        C1231g.m62e(this.adSession);
        if (!this.adSession.m222f()) {
            try {
                this.adSession.start();
            } catch (Exception unused) {
            }
        }
        if (this.adSession.m222f()) {
            this.adSession.m217k();
        }
    }

    public void loaded() {
        C1231g.m69a(this.adSession);
        C1231g.m62e(this.adSession);
        this.adSession.m216l();
    }

    public void loaded(@NonNull VastProperties vastProperties) {
        C1231g.m68a(vastProperties, "VastProperties is null");
        C1231g.m69a(this.adSession);
        C1231g.m62e(this.adSession);
        this.adSession.m230a(vastProperties.m215a());
    }
}
