//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.adsession;

import com.iab.omid.library.vungle.utils.c;
import com.iab.omid.library.vungle.utils.g;
import java.net.URL;
import org.json.JSONObject;

public final class VerificationScriptResource {
    private final String vendorKey;
    private final URL resourceUrl;
    private final String verificationParameters;

    private VerificationScriptResource(String var1, URL var2, String var3) {
        this.vendorKey = var1;
        this.resourceUrl = var2;
        this.verificationParameters = var3;
    }

    public static VerificationScriptResource createVerificationScriptResourceWithParameters(String var0, URL var1, String var2) {
        g.a(var0, "VendorKey is null or empty");
        g.a(var1, "ResourceURL is null");
        g.a(var2, "VerificationParameters is null or empty");
        return new VerificationScriptResource(var0, var1, var2);
    }

    public static VerificationScriptResource createVerificationScriptResourceWithoutParameters(URL var0) {
        g.a(var0, "ResourceURL is null");
        return new VerificationScriptResource((String)null, var0, (String)null);
    }

    public String getVendorKey() {
        return this.vendorKey;
    }

    public URL getResourceUrl() {
        return this.resourceUrl;
    }

    public String getVerificationParameters() {
        return this.verificationParameters;
    }

    public JSONObject toJsonObject() {
        JSONObject var1;
        JSONObject var10000 = var1 = new JSONObject();
        c.a(var1, "vendorKey", this.vendorKey);
        c.a(var1, "resourceUrl", this.resourceUrl.toString());
        c.a(var10000, "verificationParameters", this.verificationParameters);
        return var10000;
    }
}
