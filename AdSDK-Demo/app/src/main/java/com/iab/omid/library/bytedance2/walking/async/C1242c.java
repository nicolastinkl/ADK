package com.iab.omid.library.bytedance2.walking.async;

import com.iab.omid.library.bytedance2.walking.async.AbstractAsyncTaskC1239b;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* renamed from: com.iab.omid.library.bytedance2.walking.async.c */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/walking/async/c.class */
public class C1242c implements AbstractAsyncTaskC1239b.InterfaceC1240a {

    /* renamed from: a */
    private final BlockingQueue<Runnable> f6813a;

    /* renamed from: b */
    private final ThreadPoolExecutor f6814b;

    /* renamed from: c */
    private final ArrayDeque<AbstractAsyncTaskC1239b> f6815c = new ArrayDeque<>();

    /* renamed from: d */
    private AbstractAsyncTaskC1239b f6816d = null;

    public C1242c() {
        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
        this.f6813a = linkedBlockingQueue;
        this.f6814b = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.SECONDS, linkedBlockingQueue);
    }

    /* renamed from: a */
    private void m13a() {
        AbstractAsyncTaskC1239b poll = this.f6815c.poll();
        this.f6816d = poll;
        if (poll != null) {
            poll.m14a(this.f6814b);
        }
    }

    /* renamed from: b */
    public void m11b(AbstractAsyncTaskC1239b abstractAsyncTaskC1239b) {
        abstractAsyncTaskC1239b.m15a(this);
        this.f6815c.add(abstractAsyncTaskC1239b);
        if (this.f6816d == null) {
            m13a();
        }
    }

    @Override // com.iab.omid.library.bytedance2.walking.async.AbstractAsyncTaskC1239b.InterfaceC1240a
    /* renamed from: a */
    public void mo12a(AbstractAsyncTaskC1239b abstractAsyncTaskC1239b) {
        this.f6816d = null;
        m13a();
    }
}
