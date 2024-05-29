//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.walking.async;

import android.text.TextUtils;
import com.iab.omid.library.vungle.internal.c;
import java.util.HashSet;
import java.util.Iterator;
import org.json.JSONObject;

public class f extends a {


    public f(b1 var1, HashSet<String> var2, JSONObject var3, long var4) {
        super(var1, var2, var3, var4);
    }

    private void b(String var1) {
        com.iab.omid.library.vungle.internal.c var2;
        if ((var2 =  com.iab.omid.library.vungle.internal.c.c()) != null) {
            Iterator var4 = var2.b().iterator();

            while(var4.hasNext()) {
                com.iab.omid.library.vungle.adsession.a var3 = (com.iab.omid.library.vungle.adsession.a)var4.next();
                if (super.c.contains(var3.getAdSessionId())) {
                    var3.getAdSessionStatePublisher().b(var1, super.e);
                }
            }
        }

    }

    protected String a(Object... var1) {
        if (com.iab.omid.library.vungle.utils.c.h(super.d, super.b.a())) {
            return null;
        } else {
            super.b.a(super.d);
            return super.d.toString();
        }
    }

    protected void a(String var1) {
        if (!TextUtils.isEmpty(var1)) {
            this.b(var1);
        }

        super.a(var1);
    }

    @Override
    protected String doInBackground(Object... objects) {
        return null;
    }
}
