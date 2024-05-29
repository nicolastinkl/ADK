//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.iab.omid.library.vungle.adsession.OutputDeviceStatus;
import com.iab.omid.library.vungle.internal.e;
import com.iab.omid.library.vungle.walking.a;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class c {
    private static WindowManager a;
    private static String[] b = new String[]{"x", "y", "width", "height"};
    static float c;

    public static void a(Context var0) {
        if (var0 != null) {
            c = var0.getResources().getDisplayMetrics().density;
            a = (WindowManager)var0.getSystemService(Context.WINDOW_SERVICE);
        }

    }

    public static JSONObject a(int var0, int var1, int var2, int var3) {
        JSONObject var10000 = new JSONObject();
        JSONObject var10001 = var10000;
        JSONObject var10002 = var10000;
        JSONObject var4;
        JSONObject var10003 = var4 = var10000;

        JSONException var15;
        label77: {
            boolean var16;
            String var10004;
            float var10005;
            var10004 = "x";
            var10005 = a(var0);

            double var13 = (double)var10005;

            try {
                var10003.put(var10004, var13);
            } catch (JSONException var11) {
                var15 = var11;
                var16 = false;
                break label77;
            }

            String var20;
            float var24;
            var20 = "y";
            var24 = a(var1);

            double var25 = (double)var24;

            try {
                var10002.put(var20, var25);
            } catch (JSONException var9) {
                var15 = var9;
                var16 = false;
                break label77;
            }

            String var17;
            float var21;
            var17 = "width";
            var21 = a(var2);

            double var23 = (double)var21;

            try {
                var10001.put(var17, var23);
            } catch (JSONException var7) {
                var15 = var7;
                var16 = false;
                break label77;
            }

            float var18;
            String var22;
            var22 = "height";
            var18 = a(var3);

            double var19 = (double)var18;

            try {
                var10000.put(var22, var19);
                return var4;
            } catch (JSONException var5) {
                var15 = var5;
                var16 = false;
            }
        }

        JSONException var14 = var15;
        d.a("Error with creating viewStateObject", var14);
        return var4;
    }

    static float a(int var0) {
        return (float)var0 / c;
    }

    public static void a(JSONObject var0, String var1, Object var2) {
        Object var5;
        try {
            var0.put(var1, var2);
            return;
        } catch (NullPointerException var3) {
            var5 = var3;
        } catch (JSONException var4) {
            var5 = var4;
        }

        d.a("JSONException during JSONObject.put for name [" + var1 + "]", (Exception)var5);
    }

    public static void a(JSONObject var0, String var1) {
        try {
            var0.put("adSessionId", var1);
        } catch (JSONException var2) {
            d.a("Error with setting ad session id", var2);
        }

    }

    public static void b(JSONObject var0, String var1) {
        try {
            var0.put("notVisibleReason", var1);
        } catch (JSONException var2) {
            d.a("Error with setting not visible reason", var2);
        }

    }

    public static void a(JSONObject var0, OutputDeviceStatus var1) {
        JSONObject var10000 = var0;
        boolean var10001 = a(var1);

        try {
            var10000.put("noOutputDevice", var10001);
        } catch (JSONException var2) {
            d.a("Error with setting output device status", var2);
        }

    }

    public static void a(JSONObject var0, Boolean var1) {
        try {
            var0.put("hasWindowFocus", var1);
        } catch (JSONException var2) {
            d.a("Error with setting has window focus", var2);
        }

    }

    public static void a(JSONObject var0, a.a1 var1) {
        a.a1 var10000 = var1;
        e var12 = var1.a();
        JSONArray var2;
        var2 = new JSONArray();
        Iterator var3 = var10000.b().iterator();

        while(var3.hasNext()) {
            var2.put((String)var3.next());
        }

        JSONException var14;
        label76: {
            JSONObject var15;
            e var16;
            boolean var10001;
            JSONObject var10002;
            e var10003;
            JSONObject var10004;
            e var10005;
            try {
                var15 = var0;
                var16 = var12;
                var10002 = var0;
                var10003 = var12;
                var10004 = var0;
                var10005 = var12;
                var0.put("isFriendlyObstructionFor", var2);
            } catch (JSONException var10) {
                var14 = var10;
                var10001 = false;
                break label76;
            }

            String var10006;
            var10006 = "friendlyObstructionClass";

            String var11 = var10006;

            try {
                var10004.put(var11, var10005.d());
            } catch (JSONException var8) {
                var14 = var8;
                var10001 = false;
                break label76;
            }

            String var18;
            var18 = "friendlyObstructionPurpose";

            var11 = var18;

            try {
                var10002.put(var11, var10003.b());
            } catch (JSONException var6) {
                var14 = var6;
                var10001 = false;
                break label76;
            }

            String var17;
            var17 = "friendlyObstructionReason";

            var11 = var17;

            try {
                var15.put(var11, var16.a());
                return;
            } catch (JSONException var4) {
                var14 = var4;
                var10001 = false;
            }
        }

        JSONException var13 = var14;
        d.a("Error with setting friendly obstruction", var13);
    }

    public static void a(JSONObject var0, JSONObject var1) {
        JSONException var10000;
        label41: {
            boolean var10001;
            JSONArray var7;
            var7 = var0.optJSONArray("childViews");

            JSONArray var2 = var7;
            if (var7 == null) {
                JSONObject var8;
                JSONArray var9;
                var8 = var0;
                var9 = new JSONArray();

                var2 = var9;

                try {

                    var8.put("childViews", var2);
                } catch (JSONException var4) {
                    var10000 = var4;
                    var10001 = false;
                    break label41;
                }
            }

            var2.put(var1);
            return;
        }

        var10000.printStackTrace();
    }



    private static b a(JSONObject var0) {
        float var1 = 0.0F;
        float var2 = 0.0F;
        if (VERSION.SDK_INT >= 17) {
            if (a != null) {
                Point var8;
                Point var10000 = var8 = new Point(0, 0);
                a.getDefaultDisplay().getRealSize(var8);
                var1 = a(var10000.x);
                var2 = a(var10000.y);
            }
        } else {
            JSONArray var9;
            if ((var9 = var0.optJSONArray("childViews")) != null) {
                int var3 = var9.length();

                for(int var4 = 0; var4 < var3; ++var4) {
                    JSONObject var5;
                    if ((var5 = var9.optJSONObject(var4)) != null) {
                        double var6 = var5.optDouble("x");
                        double var10001 = var5.optDouble("y");
                        double var10 = var5.optDouble("width");
                        double var11 = var5.optDouble("height");
                        var1 = Math.max(var1, (float)(var6 + var10));
                        var2 = Math.max(var2, (float)(var10001 + var11));
                    }
                }
            }
        }

        return new b(var1, var2);
    }

    public static void b(JSONObject var0) {
        JSONObject var10000 = var0;
        JSONObject var10001 = var0;
        b var5 = a(var0);

        JSONException var6;
        label45: {
            boolean var7;
            String var10002;
            float var10003;
            var10002 = "width";
            var10003 = var5.a;

            double var11 = (double)var10003;

            try {
                var10001.put(var10002, var11);
            } catch (JSONException var3) {
                var6 = var3;
                var7 = false;
                break label45;
            }

            String var8;
            float var9;
            var8 = "height";
            var9 = var5.b;

            double var10 = (double)var9;

            try {
                var10000.put(var8, var10);
                return;
            } catch (JSONException var1) {
                var6 = var1;
                var7 = false;
            }
        }

        var6.printStackTrace();
    }

    public static boolean h(@NonNull JSONObject var0, @Nullable JSONObject var1) {
        if (var0 == null && var1 == null) {
            return true;
        } else if (var0 != null && var1 != null) {
            return f(var0, var1) && g(var0, var1) && e(var0, var1) && d(var0, var1) && c(var0, var1) && b(var0, var1);
        } else {
            return false;
        }
    }

    private static boolean f(JSONObject var0, JSONObject var1) {
        String[] var2;
        int var3 = (var2 = b).length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String var5;
            if (var0.optDouble(var5 = var2[var4]) != var1.optDouble(var5)) {
                return false;
            }
        }

        return true;
    }

    private static boolean e(JSONObject var0, JSONObject var1) {
        return Boolean.valueOf(var0.optBoolean("noOutputDevice")).equals(var1.optBoolean("noOutputDevice"));
    }

    private static boolean d(JSONObject var0, JSONObject var1) {
        return Boolean.valueOf(var0.optBoolean("hasWindowFocus")).equals(var1.optBoolean("hasWindowFocus"));
    }

    private static boolean g(JSONObject var0, JSONObject var1) {
        return var0.optString("adSessionId", "").equals(var1.optString("adSessionId", ""));
    }

    private static boolean c(JSONObject var0, JSONObject var1) {
        JSONArray var3;
        JSONArray var10000 = var3 = var0.optJSONArray("isFriendlyObstructionFor");
        JSONArray var4 = var1.optJSONArray("isFriendlyObstructionFor");
        if (var10000 == null && var4 == null) {
            return true;
        } else if (!a(var3, var4)) {
            return false;
        } else {
            for(int var2 = 0; var2 < var3.length(); ++var2) {
                if (!var3.optString(var2, "").equals(var4.optString(var2, ""))) {
                    return false;
                }
            }

            return true;
        }
    }

    private static boolean b(JSONObject var0, JSONObject var1) {
        JSONArray var3;
        JSONArray var10000 = var3 = var0.optJSONArray("childViews");
        JSONArray var4 = var1.optJSONArray("childViews");
        if (var10000 == null && var4 == null) {
            return true;
        } else if (!a(var3, var4)) {
            return false;
        } else {
            for(int var2 = 0; var2 < var3.length(); ++var2) {
                if (!h(var3.optJSONObject(var2), var4.optJSONObject(var2))) {
                    return false;
                }
            }

            return true;
        }
    }

    private static boolean a(JSONArray var0, JSONArray var1) {
        if (var0 == null && var1 == null) {
            return true;
        } else if (var0 != null && var1 != null) {
            return var0.length() == var1.length();
        } else {
            return false;
        }
    }

    private static boolean a(OutputDeviceStatus var0) {
        return OutputDeviceStatus.NOT_DETECTED == var0;

//        return SyntheticClass_1.a[var0.ordinal()] == 1;
    }

    static {
        c = Resources.getSystem().getDisplayMetrics().density;
    }

    private static class b {
        final float a;
        final float b;

        b(float var1, float var2) {
            this.a = var1;
            this.b = var2;
        }
    }
}
