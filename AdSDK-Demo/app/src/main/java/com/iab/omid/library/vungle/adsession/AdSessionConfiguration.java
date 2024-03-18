package com.iab.omid.library.vungle.adsession;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



import com.iab.omid.library.vungle.adsession.CreativeType;
import com.iab.omid.library.vungle.adsession.ImpressionType;
import com.iab.omid.library.vungle.adsession.Owner;
import com.iab.omid.library.vungle.utils.c;
import com.iab.omid.library.vungle.utils.g;
import org.json.JSONObject;

public class AdSessionConfiguration {
    private final Owner impressionOwner;
    private final Owner mediaEventsOwner;
    private final boolean isolateVerificationScripts;
    private final CreativeType creativeType;
    private final ImpressionType impressionType;

    private AdSessionConfiguration(CreativeType var1, ImpressionType var2, Owner var3, Owner var4, boolean var5) {
        this.creativeType = var1;
        this.impressionType = var2;
        this.impressionOwner = var3;
        if (var4 == null) {
            this.mediaEventsOwner = Owner.NONE;
        } else {
            this.mediaEventsOwner = var4;
        }

        this.isolateVerificationScripts = var5;
    }

    public static AdSessionConfiguration createAdSessionConfiguration(CreativeType var0, ImpressionType var1, Owner var2, Owner var3, boolean var4) {
        g.a(var0, "CreativeType is null");
        g.a(var1, "ImpressionType is null");
        g.a(var2, "Impression owner is null");
        g.a(var2, var0, var1);
        return new AdSessionConfiguration(var0, var1, var2, var3, var4);
    }

    public boolean isNativeImpressionOwner() {
        return Owner.NATIVE == this.impressionOwner;
    }

    public boolean isNativeMediaEventsOwner() {
        return Owner.NATIVE == this.mediaEventsOwner;
    }

    public JSONObject toJsonObject() {

        JSONObject   var1 = new JSONObject();

        c.a(var1, "impressionOwner", this.impressionOwner);
        c.a(var1, "mediaEventsOwner", this.mediaEventsOwner);
        c.a(var1, "creativeType", this.creativeType);
        c.a(var1, "impressionType", this.impressionType);
        c.a(var1, "isolateVerificationScripts", this.isolateVerificationScripts);
        return var1;
    }
}
