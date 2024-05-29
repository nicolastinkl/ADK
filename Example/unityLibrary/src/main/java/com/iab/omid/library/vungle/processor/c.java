//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.processor;

import android.view.View;
import androidx.annotation.NonNull;
import com.iab.omid.library.vungle.utils.e;
import com.iab.omid.library.vungle.utils.h;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import org.json.JSONObject;

public class c implements a {
    private final a a;

    public c(a var1) {
        this.a = var1;
    }

    public JSONObject a(View var1) {
        JSONObject var10000 = com.iab.omid.library.vungle.utils.c.a(0, 0, 0, 0);
        com.iab.omid.library.vungle.utils.c.a(var10000, e.a());
        return var10000;
    }

    @Override
    public void a(View var1, JSONObject var2, a var3, boolean var4, boolean var5) {
        Iterator var6 = this.a().iterator();

        while(var6.hasNext()) {
            View var7 = (View)var6.next();
            var3.a(var7, this.a, var2, var5);
        }

    }

    @Override
    public void a(View var1, com.iab.omid.library.vungle.processor.a var2, JSONObject var3, boolean var4) {
        a();
    }

    @NonNull
    ArrayList<View> a() {
        ArrayList var6;
        var6 = new ArrayList();
        com.iab.omid.library.vungle.internal.c var1;
        if ((var1 = com.iab.omid.library.vungle.internal.c.c()) != null) {
            Collection var10000 = var1.a();
            int var7 = var10000.size() * 2 + 3;
            IdentityHashMap var2;
            var2 = new IdentityHashMap(var7);
            Iterator var8 = var10000.iterator();

            while(true) {
                View var3;
                do {
                    do {
                        do {
                            do {
                                if (!var8.hasNext()) {
                                    return var6;
                                }
                            } while((var3 = ((com.iab.omid.library.vungle.adsession.a)var8.next()).c()) == null);
                        } while(!h.e(var3));
                    } while((var3 = var3.getRootView()) == null);
                } while(var2.containsKey(var3));

                var2.put(var3, var3);
                float var4 = h.c(var3);

                int var5;
                for(var5 = var6.size(); var5 > 0 && h.c((View)var6.get(var5 - 1)) > var4; --var5) {
                }

                var6.add(var5, var3);
            }
        } else {
            return var6;
        }
    }
}
