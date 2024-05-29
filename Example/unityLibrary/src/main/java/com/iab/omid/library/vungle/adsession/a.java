//package com.iab.omid.library.vungle.adsession;//
package com.iab.omid.library.vungle.adsession;
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iab.omid.library.vungle.adsession.AdSession;
import com.iab.omid.library.vungle.adsession.AdSessionConfiguration;
import com.iab.omid.library.vungle.adsession.AdSessionContext;
import com.iab.omid.library.vungle.adsession.AdSessionContextType;
import com.iab.omid.library.vungle.adsession.ErrorType;
import com.iab.omid.library.vungle.adsession.FriendlyObstructionPurpose;
import com.iab.omid.library.vungle.adsession.PossibleObstructionListener;
import com.iab.omid.library.vungle.internal.c;
import com.iab.omid.library.vungle.internal.e;
import com.iab.omid.library.vungle.internal.h;
import com.iab.omid.library.vungle.publisher.AdSessionStatePublisher;
import com.iab.omid.library.vungle.publisher.b;
import com.iab.omid.library.vungle.utils.g;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class a extends AdSession {
    private static final Pattern l = Pattern.compile("^[a-zA-Z0-9 ]+$");
    private AdSessionContext a  = new AdSessionContext();
    private  AdSessionConfiguration b;
    private final List<e> c = new ArrayList();
    private com.iab.omid.library.vungle.weakreference.a d;
    private AdSessionStatePublisher e;
    private boolean f = false;
    private boolean g = false;
    private  String h;
    private boolean i;
    private boolean j;
    private PossibleObstructionListener k;

    a(AdSessionConfiguration var1, AdSessionContext var2) {
        this.b = var1;
        this.a = var2;
        this.h = UUID.randomUUID().toString();
        this.d((View)null);
        if (var2.getAdSessionContextType() != AdSessionContextType.HTML && var2.getAdSessionContextType() != AdSessionContextType.HTML_JS) {

//            b var10001 = var3 = new b;
            AdSessionContext var10002 = var2;
            Map var4 = var2.getInjectedResourcesMap();
            b var3 = new b(var4,var10002.getOmidJsScriptContent());
            //AdSessionConfiguration var0, AdSessionContext var1)
            //var10001 = new AdSession(var1, var10002)).getOmidJsScriptContent());
            this.e = var3;
        } else {
            this.e = new com.iab.omid.library.vungle.publisher.a(var2.getWebView());
        }
    }


    private e b(View var1) {
        Iterator var3 = this.c.iterator();

        e var2;
        do {
            if (!var3.hasNext()) {
                return null;
            }
        } while((var2 = (e)var3.next()).c().get() != var1);

        return var2;
    }

    private void a() {
        if (this.i) {
            throw new IllegalStateException("Impression event can only be sent once");
        }
    }

    private void b() {
        if (this.j) {
            throw new IllegalStateException("Loaded event can only be sent once");
        }
    }

    private static void a(View var0) {
        if (var0 == null) {
            throw new IllegalArgumentException("FriendlyObstruction is null");
        }
    }

    private void a(String var1) {
        if (var1 != null) {
            if (var1.length() > 50) {
                throw new IllegalArgumentException("FriendlyObstruction has detailed reason over 50 characters in length");
            }

            if (!l.matcher(var1).matches()) {
                throw new IllegalArgumentException("FriendlyObstruction has detailed reason that contains characters not in [a-z][A-Z][0-9] or space");
            }
        }

    }

    private void d(View var1) {
        a var10000 = this;
        com.iab.omid.library.vungle.weakreference.a var2;
        var2 = new com.iab.omid.library.vungle.weakreference.a(var1);
        var10000.d = var2;
    }

    private void c(View var1) {
        Collection var2;
        if ((var2 = com.iab.omid.library.vungle.internal.c.c().b()) != null && !var2.isEmpty()) {
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
                a var3;
                if ((var3 = (a)var4.next()) != this && var3.c() == var1) {
                    var3.d.clear();
                }
            }
        }

    }

    public void start() {
        if (!this.f) {
            this.f = true;
            com.iab.omid.library.vungle.internal.c.c().c(this);
            float var1 = com.iab.omid.library.vungle.internal.h.c().b();
            this.e.a(var1);
            this.e.a(com.iab.omid.library.vungle.internal.a.a().b());
            this.e.a(this, this.a);
        }
    }

    public void error(ErrorType var1, String var2) {
        if (!this.g) {
            com.iab.omid.library.vungle.utils.g.a(var1, "Error type is null");
            com.iab.omid.library.vungle.utils.g.a(var2, "Message is null");
            this.getAdSessionStatePublisher().a(var1, var2);
        } else {
            throw new IllegalStateException("AdSession is finished");
        }
    }

    public void registerAdView(View var1) {
        if (!this.g) {
            com.iab.omid.library.vungle.utils.g.a(var1, "AdView is null");
            if (this.c() != var1) {
                this.d(var1);
                this.getAdSessionStatePublisher().a();
                this.c(var1);
            }
        }
    }

    public void finish() {
        if (!this.g) {
            this.d.clear();
            this.removeAllFriendlyObstructions();
            this.g = true;
            this.getAdSessionStatePublisher().f();
            com.iab.omid.library.vungle.internal.c.c().b(this);
            this.getAdSessionStatePublisher().b();
            this.e = null;
            this.k = null;
        }
    }

    public void addFriendlyObstruction(View var1, FriendlyObstructionPurpose var2, @Nullable String var3) {
        if (!this.g) {
            a(var1);
            this.a(var3);
            if (this.b(var1) == null) {
                this.c.add(new e(var1, var2, var3));
            }

        }
    }

    public void removeFriendlyObstruction(View var1) {
        if (!this.g) {
            a(var1);
            e var2;
            if ((var2 = this.b(var1)) != null) {
                this.c.remove(var2);
            }

        }
    }

    public void removeAllFriendlyObstructions() {
        if (!this.g) {
            this.c.clear();
        }
    }

    public List<e> d() {
        return this.c;
    }

    public void setPossibleObstructionListener(PossibleObstructionListener var1) {
        this.k = var1;
    }

    public boolean e() {
        return this.k != null;
    }

    public void a(List<com.iab.omid.library.vungle.weakreference.a> var1) {
        if (this.e()) {
            List var10000 = var1;
            ArrayList var4;
            var4 = new ArrayList();
            Iterator var2 = var10000.iterator();

            while(var2.hasNext()) {
                View var3;
                if ((var3 = (View)((com.iab.omid.library.vungle.weakreference.a)var2.next()).get()) != null) {
                    var4.add(var3);
                }
            }

            this.k.onPossibleObstructionsDetected(this.h, var4);
        }

    }

    void k() {
        this.a();
        this.getAdSessionStatePublisher().g();
        this.i = true;
    }

    void l() {
        this.b();
        this.getAdSessionStatePublisher().h();
        this.j = true;
    }

    void a(@NonNull JSONObject var1) {
        this.b();
        this.getAdSessionStatePublisher().a(var1);
        this.j = true;
    }

    public AdSessionStatePublisher getAdSessionStatePublisher() {
        return this.e;
    }

    public String getAdSessionId() {
        return this.h;
    }

    public View c() {
        return (View)this.d.get();
    }

    public boolean f() {
        return this.f && !this.g;
    }

    public boolean j() {
        return this.f;
    }

    public boolean g() {
        return this.g;
    }

    public boolean h() {
        return this.b.isNativeImpressionOwner();
    }

    public boolean i() {
        return this.b.isNativeMediaEventsOwner();
    }
}
