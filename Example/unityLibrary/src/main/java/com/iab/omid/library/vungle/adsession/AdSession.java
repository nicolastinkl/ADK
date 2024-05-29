//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.adsession;

import android.view.View;
import androidx.annotation.Nullable;
import com.iab.omid.library.vungle.publisher.AdSessionStatePublisher;
import com.iab.omid.library.vungle.utils.g;

import java.util.Map;

public abstract class AdSession {
    public AdSession() {
    }

    public static AdSession createAdSession(AdSessionConfiguration var0, AdSessionContext var1) {
        g.a();
        g.a(var0, "AdSessionConfiguration is null");
        g.a(var1, "AdSessionContext is null");
        return new a(var0, var1);
    }

    public abstract void start();

    public abstract void error(ErrorType var1, String var2);

    public abstract void registerAdView(View var1);

    public abstract void finish();

    public abstract void addFriendlyObstruction(View var1, FriendlyObstructionPurpose var2, @Nullable String var3);

    public abstract void removeFriendlyObstruction(View var1);

    public abstract void removeAllFriendlyObstructions();

    public abstract void setPossibleObstructionListener(PossibleObstructionListener var1);

    public abstract AdSessionStatePublisher getAdSessionStatePublisher();

    public abstract String getAdSessionId();
}
