//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.walking.async;

import android.os.AsyncTask;
import java.util.concurrent.ThreadPoolExecutor;
import org.json.JSONObject;

public abstract class b extends AsyncTask<Object, Void, String> {
    private com.iab.omid.library.vungle.walking.async.b.a a;
    protected final com.iab.omid.library.vungle.walking.async.b.b1 b;

    public b(com.iab.omid.library.vungle.walking.async.b.b1 var1) {
        this.b = var1;
    }

    public void a(com.iab.omid.library.vungle.walking.async.b.a var1) {
        this.a = var1;
    }

    public void a(ThreadPoolExecutor var1) {
        this.executeOnExecutor(var1, new Object[0]);
    }

    protected void a(String var1) {
        com.iab.omid.library.vungle.walking.async.b.a var2;
        if ((var2 = this.a) != null) {
            var2.a(this);
        }

    }

    public interface b1 {
        JSONObject a();

        void a(JSONObject var1);
    }

    public interface a {
        void a(com.iab.omid.library.vungle.walking.async.b var1);
    }
}
