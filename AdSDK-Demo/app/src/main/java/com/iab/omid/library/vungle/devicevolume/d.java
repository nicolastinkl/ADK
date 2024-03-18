//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.devicevolume;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings.System;

public final class d extends ContentObserver {
    private final Context a;
    private final AudioManager b;
    private final a c;
    private final c d;
    private float e;

    public d(Handler var1, Context var2, a var3, c var4) {
        super(var1);
        this.a = var2;
        this.b = (AudioManager)var2.getSystemService("audio");
        this.c = var3;
        this.d = var4;
    }

    private float a() {
        d var10000 = this;
        d var10001 = this;
        int var2 = this.b.getStreamVolume(3);
        int var1 = var10001.b.getStreamMaxVolume(3);
        return var10000.c.a(var2, var1);
    }

    private boolean a(float var1) {
        return var1 != this.e;
    }

    private void b() {
        this.d.a(this.e);
    }

    public void onChange(boolean var1) {
        super.onChange(var1);
        float var2;
        if (this.a(var2 = this.a())) {
            this.e = var2;
            this.b();
        }

    }

    public void c() {
        this.e = this.a();
        this.b();
        this.a.getContentResolver().registerContentObserver(System.CONTENT_URI, true, this);
    }

    public void d() {
        this.a.getContentResolver().unregisterContentObserver(this);
    }
}
