package com.iab.omid.library.bytedance2.adsession;

import android.view.View;
import androidx.annotation.Nullable;
import com.iab.omid.library.bytedance2.publisher.AdSessionStatePublisher;
import com.iab.omid.library.bytedance2.utils.C1231g;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/AdSession.class */
public abstract class AdSession {
    public static AdSession createAdSession(AdSessionConfiguration adSessionConfiguration, AdSessionContext adSessionContext) {
        C1231g.m71a();
        C1231g.m68a(adSessionConfiguration, "AdSessionConfiguration is null");
        C1231g.m68a(adSessionContext, "AdSessionContext is null");
        return new C1195a(adSessionConfiguration, adSessionContext);
    }

    public abstract void start();

    public abstract void error(ErrorType errorType, String str);

    public abstract void registerAdView(View view);

    public abstract void finish();

    public abstract void addFriendlyObstruction(View view, FriendlyObstructionPurpose friendlyObstructionPurpose, @Nullable String str);

    public abstract void removeFriendlyObstruction(View view);

    public abstract void removeAllFriendlyObstructions();

    public abstract void setPossibleObstructionListener(PossibleObstructionListener possibleObstructionListener);

    public abstract AdSessionStatePublisher getAdSessionStatePublisher();

    public abstract String getAdSessionId();
}
