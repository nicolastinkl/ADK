package com.iab.omid.library.vungle.adsession.media;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



import com.iab.omid.library.vungle.adsession.AdSession;

import com.iab.omid.library.vungle.internal.h;
import com.iab.omid.library.vungle.utils.c;
import com.iab.omid.library.vungle.utils.g;
import org.json.JSONObject;
import  com.iab.omid.library.vungle.adsession.a;
public final class MediaEvents {
    private final AdSession adSession;

    private MediaEvents(AdSession adSession) {
        this.adSession = adSession;
    }

    public static MediaEvents createMediaEvents(AdSession adSession) {
        a var10000 = (a)adSession;
        a var10001 = (a)adSession;
        a var1;
        a var10002 = var1 = (a)adSession;
        g.a(adSession, "AdSession is null");
        g.f(var10001);
        g.c(var10002);
        g.b(var10001);
        g.h(var10000);
        MediaEvents var2;
        MediaEvents var3 = var2 = new MediaEvents(adSession);
        var1.getAdSessionStatePublisher().a(var2.toString());
        return var3;
    }

    private void confirmValidDuration(float var1) {
        if (var1 <= 0.0F) {
            throw new IllegalArgumentException("Invalid Media duration");
        }
    }

    private void confirmValidVolume(float var1) {
        if (var1 < 0.0F || var1 > 1.0F) {
            throw new IllegalArgumentException("Invalid Media volume");
        }
    }

    public void start(float var1, float var2) {
        this.confirmValidDuration(var1);
        this.confirmValidVolume(var2);
        g.a((a) MediaEvents.this.adSession);
        JSONObject var3 = new JSONObject();
        c.a(var3, "duration", var1);
        c.a(var3, "mediaPlayerVolume", var2);
        c.a(var3, "deviceVolume", h.c().b());
        this.adSession.getAdSessionStatePublisher().a("start", var3);
    }


    public void firstQuartile() {
        g.a((a) this.adSession);
        this.adSession.getAdSessionStatePublisher().a("firstQuartile");
    }

    public void midpoint() {
        g.a((a) this.adSession);
        this.adSession.getAdSessionStatePublisher().a("midpoint");
    }

    public void thirdQuartile() {
        g.a((a) this.adSession);
        this.adSession.getAdSessionStatePublisher().a("thirdQuartile");
    }

    public void complete() {
        g.a((a) this.adSession);
        this.adSession.getAdSessionStatePublisher().a("complete");
    }

    public void pause() {
        g.a((a) this.adSession);
        this.adSession.getAdSessionStatePublisher().a("pause");
    }

    public void resume() {
        g.a((a) this.adSession);
        this.adSession.getAdSessionStatePublisher().a("resume");
    }

    public void bufferStart() {
        g.a((a) this.adSession);
        this.adSession.getAdSessionStatePublisher().a("bufferStart");
    }

    public void bufferFinish() {
        g.a((a) this.adSession);
        this.adSession.getAdSessionStatePublisher().a("bufferFinish");
    }

    public void skipped() {
        g.a((a) this.adSession);
        this.adSession.getAdSessionStatePublisher().a("skipped");
    }

    public void volumeChange(float var1) {
        this.confirmValidVolume(var1);
        g.a((a) this.adSession);
        JSONObject var2 = new JSONObject();
        c.a(var2, "mediaPlayerVolume", var1);
        c.a(var2, "deviceVolume", h.c().b());
        this.adSession.getAdSessionStatePublisher().a("volumeChange", var2);
    }


    public void playerStateChange(PlayerState var1) {
        g.a(var1, "PlayerState is null");
        g.a((a) this.adSession);
        JSONObject var2 = new JSONObject();
        c.a(var2, "state", var1);
        this.adSession.getAdSessionStatePublisher().a("playerStateChange", var2);
    }

    public void adUserInteraction(InteractionType var1) {
        g.a(var1, "InteractionType is null");
        g.a((a) this.adSession);
        JSONObject var2 = new JSONObject();
        c.a(var2, "interactionType", var1);
        this.adSession.getAdSessionStatePublisher().a("adUserInteraction", var2);
    }
}
