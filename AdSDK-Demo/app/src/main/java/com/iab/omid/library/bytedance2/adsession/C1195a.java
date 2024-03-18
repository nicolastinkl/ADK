package com.iab.omid.library.bytedance2.adsession;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.iab.omid.library.bytedance2.internal.C1203a;
import com.iab.omid.library.bytedance2.internal.C1205c;
import com.iab.omid.library.bytedance2.internal.C1208e;
import com.iab.omid.library.bytedance2.internal.C1212h;
import com.iab.omid.library.bytedance2.publisher.AdSessionStatePublisher;
import com.iab.omid.library.bytedance2.publisher.C1219a;
import com.iab.omid.library.bytedance2.publisher.C1220b;
import com.iab.omid.library.bytedance2.utils.C1231g;
import com.iab.omid.library.bytedance2.weakreference.C1248a;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.json.JSONObject;

/* renamed from: com.iab.omid.library.bytedance2.adsession.a */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/adsession/a.class */
public class C1195a extends AdSession {

    /* renamed from: l */
    private static final Pattern f6698l = Pattern.compile("^[a-zA-Z0-9 ]+$");

    /* renamed from: a */
    private final AdSessionContext f6699a;

    /* renamed from: b */
    private final AdSessionConfiguration f6700b;

    /* renamed from: d */
    private C1248a f6702d;

    /* renamed from: e */
    private AdSessionStatePublisher f6703e;

    /* renamed from: i */
    private boolean f6707i;

    /* renamed from: j */
    private boolean f6708j;

    /* renamed from: k */
    private PossibleObstructionListener f6709k;

    /* renamed from: c */
    private final List<C1208e> f6701c = new ArrayList();

    /* renamed from: f */
    private boolean f6704f = false;

    /* renamed from: g */
    private boolean f6705g = false;

    /* renamed from: h */
    private final String f6706h = UUID.randomUUID().toString();

    /* JADX INFO: Access modifiers changed from: package-private */
    public C1195a(AdSessionConfiguration adSessionConfiguration, AdSessionContext adSessionContext) {
        this.f6700b = adSessionConfiguration;
        this.f6699a = adSessionContext;
        m224d(null);
        if (adSessionContext.getAdSessionContextType() == AdSessionContextType.HTML || adSessionContext.getAdSessionContextType() == AdSessionContextType.JAVASCRIPT) {
            this.f6703e = new C1219a(adSessionContext.getWebView());
        } else {
            this.f6703e = new C1220b(adSessionContext.getInjectedResourcesMap(), adSessionContext.getOmidJsScriptContent());
        }
        this.f6703e.mo107i();
        C1205c.m184c().m187a(this);
        this.f6703e.m127a(adSessionConfiguration);
    }

    /* renamed from: b */
    private C1208e m228b(View view) {
        for (C1208e c1208e : this.f6701c) {
            if (c1208e.m169c().get() == view) {
                return c1208e;
            }
        }
        return null;
    }

    /* renamed from: a */
    private void m234a() {
        if (this.f6707i) {
            throw new IllegalStateException("Impression event can only be sent once");
        }
    }

    /* renamed from: b */
    private void m229b() {
        if (this.f6708j) {
            throw new IllegalStateException("Loaded event can only be sent once");
        }
    }

    /* renamed from: a */
    private static void m233a(View view) {
        if (view == null) {
            throw new IllegalArgumentException("FriendlyObstruction is null");
        }
    }

    /* renamed from: a */
    private void m232a(String str) {
        if (str != null) {
            if (str.length() > 50) {
                throw new IllegalArgumentException("FriendlyObstruction has detailed reason over 50 characters in length");
            }
            if (!f6698l.matcher(str).matches()) {
                throw new IllegalArgumentException("FriendlyObstruction has detailed reason that contains characters not in [a-z][A-Z][0-9] or space");
            }
        }
    }

    /* renamed from: d */
    private void m224d(View view) {
        this.f6702d = new C1248a(view);
    }

    /* renamed from: c */
    private void m226c(View view) {
        Collection<C1195a> m186b = C1205c.m184c().m186b();
        if (m186b == null || m186b.isEmpty()) {
            return;
        }
        for (C1195a c1195a : m186b) {
            if (c1195a != this && c1195a.m227c() == view) {
                c1195a.f6702d.clear();
            }
        }
    }

    @Override // com.iab.omid.library.bytedance2.adsession.AdSession
    public void start() {
        if (this.f6704f) {
            return;
        }
        this.f6704f = true;
        C1205c.m184c().m183c(this);
        this.f6703e.m130a(C1212h.m141c().m142b());
        this.f6703e.m120a(C1203a.m194a().m192b());
        this.f6703e.mo110a(this, this.f6699a);
    }

    @Override // com.iab.omid.library.bytedance2.adsession.AdSession
    public void error(ErrorType errorType, String str) {
        if (this.f6705g) {
            throw new IllegalStateException("AdSession is finished");
        }
        C1231g.m68a(errorType, "Error type is null");
        C1231g.m66a(str, "Message is null");
        getAdSessionStatePublisher().m126a(errorType, str);
    }

    @Override // com.iab.omid.library.bytedance2.adsession.AdSession
    public void registerAdView(View view) {
        if (this.f6705g) {
            return;
        }
        C1231g.m68a(view, "AdView is null");
        if (m227c() == view) {
            return;
        }
        m224d(view);
        getAdSessionStatePublisher().m131a();
        m226c(view);
    }

    @Override // com.iab.omid.library.bytedance2.adsession.AdSession
    public void finish() {
        if (this.f6705g) {
            return;
        }
        this.f6702d.clear();
        removeAllFriendlyObstructions();
        this.f6705g = true;
        getAdSessionStatePublisher().m113f();
        C1205c.m184c().m185b(this);
        getAdSessionStatePublisher().mo108b();
        this.f6703e = null;
        this.f6709k = null;
    }

    @Override // com.iab.omid.library.bytedance2.adsession.AdSession
    public void addFriendlyObstruction(View view, FriendlyObstructionPurpose friendlyObstructionPurpose, @Nullable String str) {
        if (this.f6705g) {
            return;
        }
        m233a(view);
        m232a(str);
        if (m228b(view) == null) {
            this.f6701c.add(new C1208e(view, friendlyObstructionPurpose, str));
        }
    }

    @Override // com.iab.omid.library.bytedance2.adsession.AdSession
    public void removeFriendlyObstruction(View view) {
        if (this.f6705g) {
            return;
        }
        m233a(view);
        C1208e m228b = m228b(view);
        if (m228b != null) {
            this.f6701c.remove(m228b);
        }
    }

    @Override // com.iab.omid.library.bytedance2.adsession.AdSession
    public void removeAllFriendlyObstructions() {
        if (this.f6705g) {
            return;
        }
        this.f6701c.clear();
    }

    /* renamed from: d */
    public List<C1208e> m225d() {
        return this.f6701c;
    }

    @Override // com.iab.omid.library.bytedance2.adsession.AdSession
    public void setPossibleObstructionListener(PossibleObstructionListener possibleObstructionListener) {
        this.f6709k = possibleObstructionListener;
    }

    /* renamed from: e */
    public boolean m223e() {
        return this.f6709k != null;
    }

    /* renamed from: a */
    public void m231a(List<C1248a> list) {
        if (m223e()) {
            ArrayList arrayList = new ArrayList();
            for (C1248a c1248a : list) {
                View view = c1248a.get();
                if (view != null) {
                    arrayList.add(view);
                }
            }
            this.f6709k.onPossibleObstructionsDetected(this.f6706h, arrayList);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: k */
    public void m217k() {
        m234a();
        getAdSessionStatePublisher().m112g();
        this.f6707i = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: l */
    public void m216l() {
        m229b();
        getAdSessionStatePublisher().m111h();
        this.f6708j = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: a */
    public void m230a(@NonNull JSONObject jSONObject) {
        m229b();
        getAdSessionStatePublisher().m119a(jSONObject);
        this.f6708j = true;
    }

    @Override // com.iab.omid.library.bytedance2.adsession.AdSession
    public AdSessionStatePublisher getAdSessionStatePublisher() {
        return this.f6703e;
    }

    @Override // com.iab.omid.library.bytedance2.adsession.AdSession
    public String getAdSessionId() {
        return this.f6706h;
    }

    /* renamed from: c */
    public View m227c() {
        return this.f6702d.get();
    }

    /* renamed from: f */
    public boolean m222f() {
        return this.f6704f && !this.f6705g;
    }

    /* renamed from: j */
    public boolean m218j() {
        return this.f6704f;
    }

    /* renamed from: g */
    public boolean m221g() {
        return this.f6705g;
    }

    /* renamed from: h */
    public boolean m220h() {
        return this.f6700b.isNativeImpressionOwner();
    }

    /* renamed from: i */
    public boolean m219i() {
        return this.f6700b.isNativeMediaEventsOwner();
    }
}
