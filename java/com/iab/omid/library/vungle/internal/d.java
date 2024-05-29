//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.internal;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

public class d implements Application.ActivityLifecycleCallbacks {
    private boolean a;
    protected boolean b;
    private a c;

    public d() {
    }

    private boolean a() {
        return this.b().importance == 100 || this.d();
    }

    private void a(boolean var1) {
        if (this.b != var1) {
            this.b = var1;
            if (this.a) {
                this.b(var1);
                a var2;
                if ((var2 = this.c) != null) {
                    var2.a(var1);
                }
            }
        }

    }

    public void onActivityCreated(Activity var1, Bundle var2) {
    }

    public void onActivityStarted(Activity var1) {
        this.a(true);
    }

    public void onActivityResumed(Activity var1) {
    }

    public void onActivityPaused(Activity var1) {
    }

    public void onActivityStopped(Activity var1) {
        this.a(this.a());
    }

    public void onActivitySaveInstanceState(Activity var1, Bundle var2) {
    }

    public void onActivityDestroyed(Activity var1) {
    }

    public void a(@NonNull Context var1) {
        if (var1 instanceof Application) {
            ((Application)var1).registerActivityLifecycleCallbacks(this);
        }

    }

    public void e() {
        d var10000 = this;
        this.a = true;
        boolean var1;
        this.b = var1 = this.a();
        var10000.b(var1);
    }

    public boolean c() {
        return this.b;
    }

    public void f() {
        this.a = false;
        this.c = null;
    }

    public void a(a var1) {
        this.c = var1;
    }

    protected boolean d() {
        return false;
    }

    protected void b(boolean var1) {
    }

    @VisibleForTesting
    ActivityManager.RunningAppProcessInfo b() {
        ActivityManager.RunningAppProcessInfo var10000 = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(var10000);
        return var10000;
    }

    public interface a {
        void a(boolean var1);
    }
}
