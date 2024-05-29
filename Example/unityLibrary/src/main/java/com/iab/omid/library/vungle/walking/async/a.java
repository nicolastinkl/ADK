//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle.walking.async;

import java.util.HashSet;
import org.json.JSONObject;
import com.iab.omid.library.vungle.walking.async.b;
public abstract class a extends b {
    public  HashSet<String> c;
    public  JSONObject d;
    public  long e;

    public a(b.b1 var1, HashSet<String> var2, JSONObject var3, long var4) {
        super(var1);  // 取消注释这一行，调用父类的构造方法
        this.c = new HashSet<>(var2);
        this.d = var3;
        this.e = var4;
    }

/*
    public a(b.b var1, HashSet<String> var2, JSONObject var3, long var4) {
        a var10000 = this;
        a var10001 = this;
        a var10002 = this;
//        super(var1);
        HashSet var6;
        var6 = new HashSet(var2);
        var10002.c = var6;
        var10001.d = var3;
        var10000.e = var4;
    }*/
}
