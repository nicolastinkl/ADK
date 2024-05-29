//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.walking.async;

import java.util.HashSet;
import java.util.Iterator;
import org.json.JSONObject;

public class e extends a {

    public e(b1 var1, HashSet<String> var2, JSONObject var3, long var4) {
        super(var1, var2, var3, var4);
    }

    private void b(String var1) {
        com.iab.omid.library.vungle.internal.c var2;

        if ((var2 =  com.iab.omid.library.vungle.internal.c.c()) != null) {
            Iterator var4 = var2.b().iterator();

            while(var4.hasNext()) {
                com.iab.omid.library.vungle.adsession.a var3 = (com.iab.omid.library.vungle.adsession.a)var4.next();
                if (super.c.contains(var3.getAdSessionId())) {
                    var3.getAdSessionStatePublisher().a(var1, super.e);
                }
            }
        }

    }

    protected String a(Object... var1) {
        return super.d.toString();
    }

    protected void a(String var1) {
        this.b(var1);
        super.a(var1);
    }

    @Override
    protected String doInBackground(Object... objects) {
        return null;
    }
}
