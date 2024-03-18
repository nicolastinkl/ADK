package com.iab.omid.library.bytedance2.adsession;

import com.iab.omid.library.bytedance2.utils.C1224c;
import com.iab.omid.library.bytedance2.utils.C1231g;
import java.net.URL;
import org.json.JSONObject;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/VerificationScriptResource.class */
public final class VerificationScriptResource {
    private final String vendorKey;
    private final URL resourceUrl;
    private final String verificationParameters;

    private VerificationScriptResource(String str, URL url, String str2) {
        this.vendorKey = str;
        this.resourceUrl = url;
        this.verificationParameters = str2;
    }

    public static VerificationScriptResource createVerificationScriptResourceWithParameters(String str, URL url, String str2) {
        C1231g.m66a(str, "VendorKey is null or empty");
        C1231g.m68a(url, "ResourceURL is null");
        C1231g.m66a(str2, "VerificationParameters is null or empty");
        return new VerificationScriptResource(str, url, str2);
    }

    public static VerificationScriptResource createVerificationScriptResourceWithoutParameters(URL url) {
        C1231g.m68a(url, "ResourceURL is null");
        return new VerificationScriptResource(null, url, null);
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
        JSONObject jSONObject = new JSONObject();
        C1224c.m89a(jSONObject, "vendorKey", this.vendorKey);
        C1224c.m89a(jSONObject, "resourceUrl", this.resourceUrl.toString());
        C1224c.m89a(jSONObject, "verificationParameters", this.verificationParameters);
        return jSONObject;
    }
}
