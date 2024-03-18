package com.iab.omid.library.vungle.walking;

import androidx.annotation.VisibleForTesting;

import com.iab.omid.library.vungle.walking.async.c;
import com.iab.omid.library.vungle.walking.async.d;
import com.iab.omid.library.vungle.walking.async.e;
import com.iab.omid.library.vungle.walking.async.f;

import androidx.annotation.VisibleForTesting;
import com.iab.omid.library.vungle.walking.async.c;
import com.iab.omid.library.vungle.walking.async.d;
import com.iab.omid.library.vungle.walking.async.e;
import com.iab.omid.library.vungle.walking.async.f;
import java.util.HashSet;
import org.json.JSONObject;
import org.json.JSONObject;

import java.util.HashSet;

public class b implements com.iab.omid.library.vungle.walking.async.b.b1 {
    private JSONObject a;
    private final com.iab.omid.library.vungle.walking.async.c b;

    public b(c var1) {
        this.b = var1;
    }

    public void b(JSONObject var1, HashSet<String> var2, long var3) {
        this.b.b(new f(this, var2, var1, var3));
    }

    public void a(JSONObject var1, HashSet<String> var2, long var3) {
        this.b.b(new e(this, var2, var1, var3));
    }

    public void b() {
        this.b.b(new d(this));
    }

    @VisibleForTesting
    public JSONObject a() {
        return this.a;
    }

    @VisibleForTesting
    public void a(JSONObject var1) {
        this.a = var1;
    }
}
