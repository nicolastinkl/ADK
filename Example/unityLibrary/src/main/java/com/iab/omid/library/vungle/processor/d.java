//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.processor;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import com.iab.omid.library.vungle.utils.c;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;

public class d implements a {
    private final int[] a = new int[2];

    public d() {
    }

    private void a(ViewGroup var1, JSONObject var2, a var3, boolean var4) {
        for(int var5 = 0; var5 < var1.getChildCount(); ++var5) {
            var3.a(var1.getChildAt(var5), this, var2, var4);
        }

    }

    @TargetApi(21)
    private void b(ViewGroup var1, JSONObject var2, a var3, boolean var4) {
        HashMap var5;
        var5 = new HashMap();

        for(int var6 = 0; var6 < var1.getChildCount(); ++var6) {
            View var7;
            ArrayList var8;
            if ((var8 = (ArrayList)var5.get((var7 = var1.getChildAt(var6)).getZ())) == null) {
                var8 = new ArrayList();
                var5.put(var7.getZ(), var8);
            }

            var8.add(var7);
        }

        ArrayList var10000 = new ArrayList(var5.keySet());
        Collections.sort(var10000);
        Iterator var9 = var10000.iterator();

        while(var9.hasNext()) {
            Iterator var10 = ((ArrayList)var5.get((Float)var9.next())).iterator();

            while(var10.hasNext()) {
                var3.a((View)var10.next(), this, var2, var4);
            }
        }

    }

    public JSONObject a(View var1) {
        if (var1 == null) {
            return c.a(0, 0, 0, 0);
        } else {
            d var10000 = this;
            View var10001 = var1;
            d var10002 = this;
            int var3 = var1.getWidth();
            int var4 = var1.getHeight();
            var10001.getLocationOnScreen(var10002.a);
            int[] var2;
            return c.a((var2 = var10000.a)[0], var2[1], var3, var4);
        }
    }



    @Override
    public void a(View var1, com.iab.omid.library.vungle.processor.a var2, JSONObject var3, boolean var4) {

    }


    public void a(View var1, JSONObject var2, a var3, boolean var4, boolean var5) {
        if (var1 instanceof ViewGroup) {
            ViewGroup var6 = (ViewGroup)var1;
            if (var4 && VERSION.SDK_INT >= 21) {
                this.b(var6, var2, var3, var5);
            } else {
                this.a(var6, var2, var3, var5);
            }

        }
    }
}
