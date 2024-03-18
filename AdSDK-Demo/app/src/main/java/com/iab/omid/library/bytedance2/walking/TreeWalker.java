package com.iab.omid.library.bytedance2.walking;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.annotation.VisibleForTesting;
import com.iab.omid.library.bytedance2.adsession.C1195a;
import com.iab.omid.library.bytedance2.internal.C1205c;
import com.iab.omid.library.bytedance2.processor.C1215b;
import com.iab.omid.library.bytedance2.processor.InterfaceC1213a;
import com.iab.omid.library.bytedance2.utils.C1224c;
import com.iab.omid.library.bytedance2.utils.C1230f;
import com.iab.omid.library.bytedance2.utils.C1232h;
import com.iab.omid.library.bytedance2.walking.C1236a;
import com.iab.omid.library.bytedance2.walking.async.C1242c;
import com.iab.omid.library.bytedance2.weakreference.C1248a;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/TreeWalker.class */
public class TreeWalker implements InterfaceC1213a.InterfaceC1214a {

    /* renamed from: i */
    private static TreeWalker f6783i = new TreeWalker();

    /* renamed from: j */
    private static Handler f6784j = new Handler(Looper.getMainLooper());

    /* renamed from: k */
    private static Handler f6785k = null;

    /* renamed from: l */
    private static final Runnable f6786l = new RunnableC1234b();

    /* renamed from: m */
    private static final Runnable f6787m = new RunnableC1235c();

    /* renamed from: b */
    private int f6789b;

    /* renamed from: h */
    private long f6795h;

    /* renamed from: a */
    private List<TreeWalkerTimeLogger> f6788a = new ArrayList();

    /* renamed from: c */
    private boolean f6790c = false;

    /* renamed from: d */
    private final List<C1248a> f6791d = new ArrayList();

    /* renamed from: f */
    private C1236a f6793f = new C1236a();

    /* renamed from: e */
    private C1215b f6792e = new C1215b();

    /* renamed from: g */
    private C1246b f6794g = new C1246b(new C1242c());

    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/TreeWalker$TreeWalkerNanoTimeLogger.class */
    public interface TreeWalkerNanoTimeLogger extends TreeWalkerTimeLogger {
        void onTreeProcessedNano(int i, long j);
    }

    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/TreeWalker$TreeWalkerTimeLogger.class */
    public interface TreeWalkerTimeLogger {
        void onTreeProcessed(int i, long j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.iab.omid.library.bytedance2.walking.TreeWalker$a */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/TreeWalker$a.class */
    public class RunnableC1233a implements Runnable {
        RunnableC1233a() {
        }

        @Override // java.lang.Runnable
        public void run() {
            TreeWalker.this.f6794g.m1b();
        }
    }

    /* renamed from: com.iab.omid.library.bytedance2.walking.TreeWalker$b */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/TreeWalker$b.class */
    static class RunnableC1234b implements Runnable {
        RunnableC1234b() {
        }

        @Override // java.lang.Runnable
        public void run() {
            TreeWalker.getInstance().m34l();
        }
    }

    /* renamed from: com.iab.omid.library.bytedance2.walking.TreeWalker$c */
    /* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/TreeWalker$c.class */
    static class RunnableC1235c implements Runnable {
        RunnableC1235c() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (TreeWalker.f6785k != null) {
                TreeWalker.f6785k.post(TreeWalker.f6786l);
                TreeWalker.f6785k.postDelayed(TreeWalker.f6787m, 200L);
            }
        }
    }

    TreeWalker() {
    }

    public static TreeWalker getInstance() {
        return f6783i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: l */
    public void m34l() {
        m41e();
        m40f();
        m42d();
    }

    /* renamed from: e */
    private void m41e() {
        this.f6789b = 0;
        this.f6791d.clear();
        this.f6790c = false;
        Iterator<C1195a> it = C1205c.m184c().m188a().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            } else if (it.next().m223e()) {
                this.f6790c = true;
                break;
            }
        }
        this.f6795h = C1230f.m72b();
    }

    /* renamed from: d */
    private void m42d() {
        m52a(C1230f.m72b() - this.f6795h);
    }

    /* renamed from: a */
    private void m51a(View view, InterfaceC1213a interfaceC1213a, JSONObject jSONObject, EnumC1247c enumC1247c, boolean z) {
        interfaceC1213a.mo134a(view, jSONObject, this, enumC1247c == EnumC1247c.PARENT_VIEW, z);
    }

    /* renamed from: b */
    private boolean m45b(View view, JSONObject jSONObject) {
        String m22d = this.f6793f.m22d(view);
        if (m22d != null) {
            C1224c.m90a(jSONObject, m22d);
            C1224c.m91a(jSONObject, Boolean.valueOf(this.f6793f.m19f(view)));
            this.f6793f.m23d();
            return true;
        }
        return false;
    }

    /* renamed from: a */
    private void m47a(String str, View view, JSONObject jSONObject) {
        InterfaceC1213a m137b = this.f6792e.m137b();
        String m26b = this.f6793f.m26b(str);
        if (m26b != null) {
            JSONObject mo135a = m137b.mo135a(view);
            C1224c.m90a(mo135a, str);
            C1224c.m86b(mo135a, m26b);
            C1224c.m88a(jSONObject, mo135a);
        }
    }

    /* renamed from: a */
    private boolean m49a(View view, JSONObject jSONObject) {
        C1236a.C1237a m24c = this.f6793f.m24c(view);
        if (m24c != null) {
            C1224c.m92a(jSONObject, m24c);
            return true;
        }
        return false;
    }

    /* renamed from: a */
    private void m52a(long j) {
        if (this.f6788a.size() > 0) {
            for (TreeWalkerTimeLogger treeWalkerTimeLogger : this.f6788a) {
                treeWalkerTimeLogger.onTreeProcessed(this.f6789b, TimeUnit.NANOSECONDS.toMillis(j));
                if (treeWalkerTimeLogger instanceof TreeWalkerNanoTimeLogger) {
                    ((TreeWalkerNanoTimeLogger) treeWalkerTimeLogger).onTreeProcessedNano(this.f6789b, j);
                }
            }
        }
    }

    /* renamed from: i */
    private void m37i() {
        if (f6785k == null) {
            Handler handler = new Handler(Looper.getMainLooper());
            f6785k = handler;
            handler.post(f6786l);
            f6785k.postDelayed(f6787m, 200L);
        }
    }

    /* renamed from: k */
    private void m35k() {
        Handler handler = f6785k;
        if (handler != null) {
            handler.removeCallbacks(f6787m);
            f6785k = null;
        }
    }

    public void addTimeLogger(TreeWalkerTimeLogger treeWalkerTimeLogger) {
        if (this.f6788a.contains(treeWalkerTimeLogger)) {
            return;
        }
        this.f6788a.add(treeWalkerTimeLogger);
    }

    public void removeTimeLogger(TreeWalkerTimeLogger treeWalkerTimeLogger) {
        if (this.f6788a.contains(treeWalkerTimeLogger)) {
            this.f6788a.remove(treeWalkerTimeLogger);
        }
    }

    /* renamed from: h */
    public void m38h() {
        m37i();
    }

    /* renamed from: j */
    public void m36j() {
        m39g();
        this.f6788a.clear();
        f6784j.post(new RunnableC1233a());
    }

    /* renamed from: g */
    public void m39g() {
        m35k();
    }

    @VisibleForTesting
    /* renamed from: f */
    void m40f() {
        this.f6793f.m21e();
        long m72b = C1230f.m72b();
        InterfaceC1213a m138a = this.f6792e.m138a();
        if (this.f6793f.m28b().size() > 0) {
            Iterator<String> it = this.f6793f.m28b().iterator();
            while (it.hasNext()) {
                String next = it.next();
                JSONObject mo135a = m138a.mo135a(null);
                m47a(next, this.f6793f.m29a(next), mo135a);
                C1224c.m87b(mo135a);
                HashSet<String> hashSet = new HashSet<>();
                hashSet.add(next);
                this.f6794g.m2a(mo135a, hashSet, m72b);
            }
        }
        if (this.f6793f.m25c().size() > 0) {
            JSONObject mo135a2 = m138a.mo135a(null);
            m51a(null, m138a, mo135a2, EnumC1247c.PARENT_VIEW, false);
            C1224c.m87b(mo135a2);
            this.f6794g.m0b(mo135a2, this.f6793f.m25c(), m72b);
            if (this.f6790c) {
                for (C1195a c1195a : C1205c.m184c().m188a()) {
                    c1195a.m231a(this.f6791d);
                }
            }
        } else {
            this.f6794g.m1b();
        }
        this.f6793f.m33a();
    }

    @Override // com.iab.omid.library.bytedance2.processor.InterfaceC1213a.InterfaceC1214a
    /* renamed from: a */
    public void mo50a(View view, InterfaceC1213a interfaceC1213a, JSONObject jSONObject, boolean z) {
        EnumC1247c m20e;
        if (C1232h.m55d(view) && (m20e = this.f6793f.m20e(view)) != EnumC1247c.UNDERLYING_VIEW) {
            JSONObject mo135a = interfaceC1213a.mo135a(view);
            C1224c.m88a(jSONObject, mo135a);
            if (!m45b(view, mo135a)) {
                boolean z2 = z || m49a(view, mo135a);
                if (this.f6790c && m20e == EnumC1247c.OBSTRUCTION_VIEW && !z2) {
                    this.f6791d.add(new C1248a(view));
                }
                m51a(view, interfaceC1213a, mo135a, m20e, z2);
            }
            this.f6789b++;
        }
    }
}
