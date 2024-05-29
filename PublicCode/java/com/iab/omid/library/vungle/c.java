//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.iab.omid.library.vungle;

import com.iab.omid.library.vungle.utils.g;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class c {
    private static final Pattern a = Pattern.compile("<(head)( [^>]*)?>", 2);
    private static final Pattern b = Pattern.compile("<(head)( [^>]*)?/>", 2);
    private static final Pattern c = Pattern.compile("<(body)( [^>]*?)?>", 2);
    private static final Pattern d = Pattern.compile("<(body)( [^>]*?)?/>", 2);
    private static final Pattern e = Pattern.compile("<(html)( [^>]*?)?>", 2);
    private static final Pattern f = Pattern.compile("<(html)( [^>]*?)?/>", 2);
    private static final Pattern g = Pattern.compile("<!DOCTYPE [^>]*>", 2);

    static String b(String var0, String var1) {
        return a(var1, "<script type=\"text/javascript\">" + var0 + "</script>");
    }

    static String a(String var0, String var1) {
        com.iab.omid.library.vungle.utils.g.a(var0, "HTML is null or empty");
        int[][] var2 = a(var0);
        int var3 = var0.length() + var1.length() + 16;
        StringBuilder var4;
        StringBuilder var10001 = var4 = new StringBuilder(var3);
        if (b(var0, var10001, b, var1, var2)) {
            return var4.toString();
        } else if (a(var0, var4, a, var1, var2)) {
            return var4.toString();
        } else if (b(var0, var4, d, var1, var2)) {
            return var4.toString();
        } else if (a(var0, var4, c, var1, var2)) {
            return var4.toString();
        } else if (b(var0, var4, f, var1, var2)) {
            return var4.toString();
        } else if (a(var0, var4, e, var1, var2)) {
            return var4.toString();
        } else {
            return a(var0, var4, g, var1, var2) ? var4.toString() : var1 + var0;
        }
    }

    private static int[][] a(String var0) {
        ArrayList var1;
        var1 = new ArrayList();
        int var2 = 0;
        int var3 = var0.length();

        while(true) {
            while(var2 < var3) {
                if ((var2 = var0.indexOf("<!--", var2)) >= 0) {
                    int[] var10001;
                    int var4;
                    if ((var4 = var0.indexOf("-->", var2)) >= 0) {
                        int[] var5;
                        var10001 = var5 = new int[2];
                        var10001[0] = var2;
                        var10001[1] = var4;
                        var1.add(var5);
                        var2 = var4 + 3;
                        continue;
                    }

                    int[] var6;
                    var10001 = var6 = new int[2];
                    var10001[0] = var2;
                    var10001[1] = var3;
                    var1.add(var6);
                }

                var2 = var3;
            }

            return (int[][])var1.toArray(new int[0][2]);
        }
    }

    private static boolean a(int var0, int[][] var1) {
        if (var1 != null) {
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                int[] var4;
                if (var0 >= (var4 = var1[var3])[0] && var0 <= var4[1]) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean a(String var0, StringBuilder var1, Pattern var2, String var3, int[][] var4) {
        Matcher var6 = var2.matcher(var0);
        int var5 = 0;

        int var10000;
        do {
            if (!var6.find(var5)) {
                return false;
            }

            var10000 = var6.start();
            var5 = var6.end();
        } while(a(var10000, var4));

        var1.append(var0.substring(0, var6.end()));
        var1.append(var3);
        var1.append(var0.substring(var6.end()));
        return true;
    }

    private static boolean b(String var0, StringBuilder var1, Pattern var2, String var3, int[][] var4) {
        Matcher var6 = var2.matcher(var0);
        int var5 = 0;

        int var10000;
        do {
            if (!var6.find(var5)) {
                return false;
            }

            var10000 = var6.start();
            var5 = var6.end();
        } while(a(var10000, var4));

        var1.append(var0.substring(0, var6.end() - 2));
        var1.append(">");
        var1.append(var3);
        var1.append("</");
        var1.append(var6.group(1));
        var1.append(">");
        var1.append(var0.substring(var6.end()));
        return true;
    }
}
