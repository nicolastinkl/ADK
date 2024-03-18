package com.iab.omid.library.bytedance2.adsession;

import com.iab.omid.library.bytedance2.utils.C1224c;
import com.iab.omid.library.bytedance2.utils.C1231g;
import org.json.JSONObject;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/AdSessionConfiguration.class */
public class AdSessionConfiguration {
    private final Owner impressionOwner;
    private final Owner mediaEventsOwner;
    private final boolean isolateVerificationScripts;
    private final CreativeType creativeType;
    private final ImpressionType impressionType;

    private AdSessionConfiguration(CreativeType creativeType, ImpressionType impressionType, Owner owner, Owner owner2, boolean z) {
        this.creativeType = creativeType;
        this.impressionType = impressionType;
        this.impressionOwner = owner;
        if (owner2 == null) {
            this.mediaEventsOwner = Owner.NONE;
        } else {
            this.mediaEventsOwner = owner2;
        }
        this.isolateVerificationScripts = z;
    }

    public static AdSessionConfiguration createAdSessionConfiguration(CreativeType creativeType, ImpressionType impressionType, Owner owner, Owner owner2, boolean z) {
        C1231g.m68a(creativeType, "CreativeType is null");
        C1231g.m68a(impressionType, "ImpressionType is null");
        C1231g.m68a(owner, "Impression owner is null");
        C1231g.m70a(owner, creativeType, impressionType);
        return new AdSessionConfiguration(creativeType, impressionType, owner, owner2, z);
    }

    public boolean isNativeImpressionOwner() {
        return Owner.NATIVE == this.impressionOwner;
    }

    public boolean isNativeMediaEventsOwner() {
        return Owner.NATIVE == this.mediaEventsOwner;
    }

    public JSONObject toJsonObject() {
        JSONObject jSONObject = new JSONObject();
        C1224c.m89a(jSONObject, "impressionOwner", this.impressionOwner);
        C1224c.m89a(jSONObject, "mediaEventsOwner", this.mediaEventsOwner);
        C1224c.m89a(jSONObject, "creativeType", this.creativeType);
        C1224c.m89a(jSONObject, "impressionType", this.impressionType);
        C1224c.m89a(jSONObject, "isolateVerificationScripts", Boolean.valueOf(this.isolateVerificationScripts));
        return jSONObject;
    }
}
