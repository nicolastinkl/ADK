package com.iab.omid.library.bytedance2.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.iab.omid.library.bytedance2.adsession.OutputDeviceStatus;
import com.iab.omid.library.bytedance2.internal.C1208e;
import com.iab.omid.library.bytedance2.walking.C1236a;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.utils.c */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/utils/c.class */
public class C1224c {

    /* renamed from: a */
    private static WindowManager f6776a;

    /* renamed from: b */
    private static String[] f6777b = {"x", "y", "width", "height"};

    /* renamed from: c */
    static float f6778c = Resources.getSystem().getDisplayMetrics().density;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.iab.omid.library.bytedance2.utils.c$a */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/utils/c$a.class */
    public static /* synthetic */ class C1225a {

        /* renamed from: a */
        static final /* synthetic */ int[] f6779a;

        static {
            int[] iArr = new int[OutputDeviceStatus.values().length];
            f6779a = iArr;
            try {
                iArr[OutputDeviceStatus.NOT_DETECTED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.iab.omid.library.bytedance2.utils.c$b */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/utils/c$b.class */
    public static class C1226b {

        /* renamed from: a */
        final float f6780a;

        /* renamed from: b */
        final float f6781b;

        C1226b(float f, float f2) {
            this.f6780a = f;
            this.f6781b = f2;
        }
    }

    /* renamed from: a */
    public static void m97a(Context context) {
        if (context != null) {
            f6778c = context.getResources().getDisplayMetrics().density;
            f6776a = (WindowManager) context.getSystemService("window");
        }
    }

    /* renamed from: a */
    public static JSONObject m98a(int i, int i2, int i3, int i4) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("x", m99a(i));
            jSONObject.put("y", m99a(i2));
            jSONObject.put("width", m99a(i3));
            jSONObject.put("height", m99a(i4));
        } catch (JSONException e) {
            C1227d.m77a("Error with creating viewStateObject", e);
        }
        return jSONObject;
    }

    /* renamed from: a */
    static float m99a(int i) {
        return i / f6778c;
    }

    /* renamed from: a */
    public static void m89a(JSONObject jSONObject, String str, Object obj) {
        try {
            jSONObject.put(str, obj);
        } catch (NullPointerException | JSONException e) {
            C1227d.m77a("JSONException during JSONObject.put for name [" + str + "]", e);
        }
    }

    /* renamed from: a */
    public static void m90a(JSONObject jSONObject, String str) {
        try {
            jSONObject.put("adSessionId", str);
        } catch (JSONException e) {
            C1227d.m77a("Error with setting ad session id", e);
        }
    }

    /* renamed from: b */
    public static void m86b(JSONObject jSONObject, String str) {
        try {
            jSONObject.put("notVisibleReason", str);
        } catch (JSONException e) {
            C1227d.m77a("Error with setting not visible reason", e);
        }
    }

    /* renamed from: a */
    public static void m93a(JSONObject jSONObject, OutputDeviceStatus outputDeviceStatus) {
        try {
            jSONObject.put("noOutputDevice", m96a(outputDeviceStatus));
        } catch (JSONException e) {
            C1227d.m77a("Error with setting output device status", e);
        }
    }

    /* renamed from: a */
    public static void m91a(JSONObject jSONObject, Boolean bool) {
        try {
            jSONObject.put("hasWindowFocus", bool);
        } catch (JSONException e) {
            C1227d.m77a("Error with setting has window focus", e);
        }
    }

    /* renamed from: a */
    public static void m92a(JSONObject jSONObject, C1236a.C1237a c1237a) {
        C1208e m18a = c1237a.m18a();
        JSONArray jSONArray = new JSONArray();
        for (String str : c1237a.m16b()) {
            jSONArray.put(str);
        }
        try {
            jSONObject.put("isFriendlyObstructionFor", jSONArray);
            jSONObject.put("friendlyObstructionClass", m18a.m168d());
            jSONObject.put("friendlyObstructionPurpose", m18a.m170b());
            jSONObject.put("friendlyObstructionReason", m18a.m171a());
        } catch (JSONException e) {
            C1227d.m77a("Error with setting friendly obstruction", e);
        }
    }

    /* renamed from: a */
    public static void m88a(JSONObject jSONObject, JSONObject jSONObject2) {
        try {
            JSONArray optJSONArray = jSONObject.optJSONArray("childViews");
            JSONArray jSONArray2 = optJSONArray;
            if (optJSONArray == null) {
                jSONArray2 = new JSONArray();
                jSONObject.put("childViews", jSONArray2);
            }
            jSONArray2.put(jSONObject2);
        } catch (JSONException unused) {
            unused.printStackTrace();
        }
    }

    /* renamed from: a */
    private static C1226b m94a(JSONObject jSONObject) {
        float f = 0.0f;
        float f2 = 0.0f;
        if (Build.VERSION.SDK_INT < 17) {
            JSONArray optJSONArray = jSONObject.optJSONArray("childViews");
            if (optJSONArray != null) {
                int length = optJSONArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                    if (optJSONObject != null) {
                        double optDouble = optJSONObject.optDouble("x");
                        double optDouble2 = optJSONObject.optDouble("y");
                        double optDouble3 = optJSONObject.optDouble("width");
                        double optDouble4 = optJSONObject.optDouble("height");
                        f = Math.max(f, (float) (optDouble + optDouble3));
                        f2 = Math.max(f2, (float) (optDouble2 + optDouble4));
                    }
                }
            }
        } else if (f6776a != null) {
            Point point = new Point(0, 0);
            f6776a.getDefaultDisplay().getRealSize(point);
            f = m99a(point.x);
            f2 = m99a(point.y);
        }
        return new C1226b(f, f2);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v0, resolved type: org.json.JSONObject */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: b */
    public static void m87b(JSONObject jSONObject) {
        C1226b m94a = m94a(jSONObject);
        try {
            jSONObject.put("width", m94a.f6780a);
            jSONObject.put("height", m94a.f6781b);
        } catch (JSONException unused) {
            unused.printStackTrace();
        }
    }

    /* renamed from: h */
    public static boolean m79h(@NonNull JSONObject jSONObject, @Nullable JSONObject jSONObject2) {
        if (jSONObject == null && jSONObject2 == null) {
            return true;
        }
        return jSONObject != null && jSONObject2 != null && m81f(jSONObject, jSONObject2) && m80g(jSONObject, jSONObject2) && m82e(jSONObject, jSONObject2) && m83d(jSONObject, jSONObject2) && m84c(jSONObject, jSONObject2) && m85b(jSONObject, jSONObject2);
    }

    /* renamed from: f */
    private static boolean m81f(JSONObject jSONObject, JSONObject jSONObject2) {
        String[] strArr;
        for (String str : f6777b) {
            if (jSONObject.optDouble(str) != jSONObject2.optDouble(str)) {
                return false;
            }
        }
        return true;
    }

    /* renamed from: e */
    private static boolean m82e(JSONObject jSONObject, JSONObject jSONObject2) {
        return Boolean.valueOf(jSONObject.optBoolean("noOutputDevice")).equals(Boolean.valueOf(jSONObject2.optBoolean("noOutputDevice")));
    }

    /* renamed from: d */
    private static boolean m83d(JSONObject jSONObject, JSONObject jSONObject2) {
        return Boolean.valueOf(jSONObject.optBoolean("hasWindowFocus")).equals(Boolean.valueOf(jSONObject2.optBoolean("hasWindowFocus")));
    }

    /* renamed from: g */
    private static boolean m80g(JSONObject jSONObject, JSONObject jSONObject2) {
        return jSONObject.optString("adSessionId", "").equals(jSONObject2.optString("adSessionId", ""));
    }

    /* renamed from: c */
    private static boolean m84c(JSONObject jSONObject, JSONObject jSONObject2) {
        JSONArray optJSONArray = jSONObject.optJSONArray("isFriendlyObstructionFor");
        JSONArray optJSONArray2 = jSONObject2.optJSONArray("isFriendlyObstructionFor");
        if (optJSONArray == null && optJSONArray2 == null) {
            return true;
        }
        if (m95a(optJSONArray, optJSONArray2)) {
            for (int i = 0; i < optJSONArray.length(); i++) {
                if (!optJSONArray.optString(i, "").equals(optJSONArray2.optString(i, ""))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /* renamed from: b */
    private static boolean m85b(JSONObject jSONObject, JSONObject jSONObject2) {
        JSONArray optJSONArray = jSONObject.optJSONArray("childViews");
        JSONArray optJSONArray2 = jSONObject2.optJSONArray("childViews");
        if (optJSONArray == null && optJSONArray2 == null) {
            return true;
        }
        if (m95a(optJSONArray, optJSONArray2)) {
            for (int i = 0; i < optJSONArray.length(); i++) {
                if (!m79h(optJSONArray.optJSONObject(i), optJSONArray2.optJSONObject(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /* renamed from: a */
    private static boolean m95a(JSONArray jSONArray, JSONArray jSONArray2) {
        if (jSONArray == null && jSONArray2 == null) {
            return true;
        }
        return (jSONArray == null || jSONArray2 == null || jSONArray.length() != jSONArray2.length()) ? false : true;
    }

    /* renamed from: a */
    private static boolean m96a(OutputDeviceStatus outputDeviceStatus) {
        return C1225a.f6779a[outputDeviceStatus.ordinal()] == 1;
    }
}
