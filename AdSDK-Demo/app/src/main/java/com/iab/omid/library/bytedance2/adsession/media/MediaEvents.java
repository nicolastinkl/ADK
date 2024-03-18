package com.iab.omid.library.bytedance2.adsession.media;

import com.iab.omid.library.bytedance2.adsession.AdSession;
import com.iab.omid.library.bytedance2.adsession.C1195a;
import com.iab.omid.library.bytedance2.internal.C1212h;
import com.iab.omid.library.bytedance2.utils.C1224c;
import com.iab.omid.library.bytedance2.utils.C1231g;
import org.json.JSONObject;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/media/MediaEvents.class */
public final class MediaEvents {
    private final C1195a adSession;

    private MediaEvents(C1195a c1195a) {
        this.adSession = c1195a;
    }

    public static MediaEvents createMediaEvents(AdSession adSession) {
        C1195a c1195a = (C1195a) adSession;
        C1231g.m68a(adSession, "AdSession is null");
        C1231g.m61f(c1195a);
        C1231g.m64c(c1195a);
        C1231g.m65b(c1195a);
        C1231g.m59h(c1195a);
        MediaEvents mediaEvents = new MediaEvents(c1195a);
        c1195a.getAdSessionStatePublisher().m124a(mediaEvents);
        return mediaEvents;
    }

    private void confirmValidDuration(float f) {
        if (f <= 0.0f) {
            throw new IllegalArgumentException("Invalid Media duration");
        }
    }

    private void confirmValidVolume(float f) {
        if (f < 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("Invalid Media volume");
        }
    }

    public void start(float f, float f2) {
        confirmValidDuration(f);
        confirmValidVolume(f2);
        C1231g.m69a(this.adSession);
        JSONObject jSONObject = new JSONObject();
        C1224c.m89a(jSONObject, "duration", Float.valueOf(f));
        C1224c.m89a(jSONObject, "mediaPlayerVolume", Float.valueOf(f2));
        C1224c.m89a(jSONObject, "deviceVolume", Float.valueOf(C1212h.m141c().m142b()));
        this.adSession.getAdSessionStatePublisher().m121a("start", jSONObject);
    }

    public void firstQuartile() {
        C1231g.m69a(this.adSession);
        this.adSession.getAdSessionStatePublisher().m123a("firstQuartile");
    }

    public void midpoint() {
        C1231g.m69a(this.adSession);
        this.adSession.getAdSessionStatePublisher().m123a("midpoint");
    }

    public void thirdQuartile() {
        C1231g.m69a(this.adSession);
        this.adSession.getAdSessionStatePublisher().m123a("thirdQuartile");
    }

    public void complete() {
        C1231g.m69a(this.adSession);
        this.adSession.getAdSessionStatePublisher().m123a("complete");
    }

    public void pause() {
        C1231g.m69a(this.adSession);
        this.adSession.getAdSessionStatePublisher().m123a("pause");
    }

    public void resume() {
        C1231g.m69a(this.adSession);
        this.adSession.getAdSessionStatePublisher().m123a("resume");
    }

    public void bufferStart() {
        C1231g.m69a(this.adSession);
        this.adSession.getAdSessionStatePublisher().m123a("bufferStart");
    }

    public void bufferFinish() {
        C1231g.m69a(this.adSession);
        this.adSession.getAdSessionStatePublisher().m123a("bufferFinish");
    }

    public void skipped() {
        C1231g.m69a(this.adSession);
        this.adSession.getAdSessionStatePublisher().m123a("skipped");
    }

    public void volumeChange(float f) {
        confirmValidVolume(f);
        C1231g.m69a(this.adSession);
        JSONObject jSONObject = new JSONObject();
        C1224c.m89a(jSONObject, "mediaPlayerVolume", Float.valueOf(f));
        C1224c.m89a(jSONObject, "deviceVolume", Float.valueOf(C1212h.m141c().m142b()));
        this.adSession.getAdSessionStatePublisher().m121a("volumeChange", jSONObject);
    }

    public void playerStateChange(PlayerState playerState) {
        C1231g.m68a(playerState, "PlayerState is null");
        C1231g.m69a(this.adSession);
        JSONObject jSONObject = new JSONObject();
        C1224c.m89a(jSONObject, "state", playerState);
        this.adSession.getAdSessionStatePublisher().m121a("playerStateChange", jSONObject);
    }

    public void adUserInteraction(InteractionType interactionType) {
        C1231g.m68a(interactionType, "InteractionType is null");
        C1231g.m69a(this.adSession);
        JSONObject jSONObject = new JSONObject();
        C1224c.m89a(jSONObject, "interactionType", interactionType);
        this.adSession.getAdSessionStatePublisher().m121a("adUserInteraction", jSONObject);
    }
}
