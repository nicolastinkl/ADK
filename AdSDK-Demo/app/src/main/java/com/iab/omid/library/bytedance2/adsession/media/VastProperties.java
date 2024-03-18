package com.iab.omid.library.bytedance2.adsession.media;

import com.iab.omid.library.bytedance2.utils.C1227d;
import com.iab.omid.library.bytedance2.utils.C1231g;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/media/VastProperties.class */
public final class VastProperties {

    /* renamed from: a */
    private final boolean f6710a;

    /* renamed from: b */
    private final Float f6711b;

    /* renamed from: c */
    private final boolean f6712c;

    /* renamed from: d */
    private final Position f6713d;

    private VastProperties(boolean z, Float f, boolean z2, Position position) {
        this.f6710a = z;
        this.f6711b = f;
        this.f6712c = z2;
        this.f6713d = position;
    }

    public static VastProperties createVastPropertiesForSkippableMedia(float f, boolean z, Position position) {
        C1231g.m68a(position, "Position is null");
        return new VastProperties(true, Float.valueOf(f), z, position);
    }

    public static VastProperties createVastPropertiesForNonSkippableMedia(boolean z, Position position) {
        C1231g.m68a(position, "Position is null");
        return new VastProperties(false, null, z, position);
    }

    public boolean isSkippable() {
        return this.f6710a;
    }

    public Float getSkipOffset() {
        return this.f6711b;
    }

    public boolean isAutoPlay() {
        return this.f6712c;
    }

    public Position getPosition() {
        return this.f6713d;
    }

    /* renamed from: a */
    public JSONObject m215a() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("skippable", this.f6710a);
            if (this.f6710a) {
                jSONObject.put("skipOffset", this.f6711b);
            }
            jSONObject.put("autoPlay", this.f6712c);
            jSONObject.put("position", this.f6713d);
        } catch (JSONException e) {
            C1227d.m77a("VastProperties: JSON error", e);
        }
        return jSONObject;
    }
}
