package com.iab.omid.library.vungle.adsession.media;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



import com.iab.omid.library.vungle.utils.d;
import com.iab.omid.library.vungle.utils.g;
import org.json.JSONException;
import org.json.JSONObject;

public final class VastProperties {
    private final boolean a;
    private final Float b;
    private final boolean c;
    private final Position d;

    private VastProperties(boolean var1, Float var2, boolean var3, Position var4) {
        this.a = var1;
        this.b = var2;
        this.c = var3;
        this.d = var4;
    }

    public static VastProperties createVastPropertiesForSkippableMedia(float var0, boolean var1, Position var2) {
        g.a(var2, "Position is null");
        return new VastProperties(true, var0, var1, var2);
    }

    public static VastProperties createVastPropertiesForNonSkippableMedia(boolean var0, Position var1) {
        g.a(var1, "Position is null");
        return new VastProperties(false, (Float)null, var0, var1);
    }

    public boolean isSkippable() {
        return this.a;
    }

    public Float getSkipOffset() {
        return this.b;
    }

    public boolean isAutoPlay() {
        return this.c;
    }

    public Position getPosition() {
        return this.d;
    }

    public JSONObject a() {
        JSONObject var1 = new JSONObject();
        try {
            var1.put("skippable", this.a);
            if (this.a) {
                var1.put("skipOffset", this.b);
            }
            var1.put("autoPlay", this.c);
            var1.put("position", this.d);
        } catch (JSONException var2) {
            com.iab.omid.library.vungle.utils.d.a("VastProperties: JSON error", var2);
        }
        return var1;
    }

}
