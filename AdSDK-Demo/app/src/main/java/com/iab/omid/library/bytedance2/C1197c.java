package com.iab.omid.library.bytedance2;

import com.iab.omid.library.bytedance2.utils.C1231g;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* renamed from: com.iab.omid.library.bytedance2.c */
/* loaded from: ads-sdk-5.5.0.9.aar:classes.jar:com/iab/omid/library/bytedance2/c.class */
class C1197c {

    /* renamed from: a */
    private static final Pattern f6715a = Pattern.compile("<(head)( [^>]*)?>", 2);

    /* renamed from: b */
    private static final Pattern f6716b = Pattern.compile("<(head)( [^>]*)?/>", 2);

    /* renamed from: c */
    private static final Pattern f6717c = Pattern.compile("<(body)( [^>]*?)?>", 2);

    /* renamed from: d */
    private static final Pattern f6718d = Pattern.compile("<(body)( [^>]*?)?/>", 2);

    /* renamed from: e */
    private static final Pattern f6719e = Pattern.compile("<(html)( [^>]*?)?>", 2);

    /* renamed from: f */
    private static final Pattern f6720f = Pattern.compile("<(html)( [^>]*?)?/>", 2);

    /* renamed from: g */
    private static final Pattern f6721g = Pattern.compile("<!DOCTYPE [^>]*>", 2);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: b */
    public static String m204b(String str, String str2) {
        return m206a(str2, "<script type=\"text/javascript\">" + str + "</script>");
    }

    /* renamed from: a */
    static String m206a(String str, String str2) {
        C1231g.m66a(str, "HTML is null or empty");
        int[][] m207a = m207a(str);
        StringBuilder sb = new StringBuilder(str.length() + str2.length() + 16);
        if (!m203b(str, sb, f6716b, str2, m207a) && !m205a(str, sb, f6715a, str2, m207a) && !m203b(str, sb, f6718d, str2, m207a) && !m205a(str, sb, f6717c, str2, m207a) && !m203b(str, sb, f6720f, str2, m207a) && !m205a(str, sb, f6719e, str2, m207a) && !m205a(str, sb, f6721g, str2, m207a)) {
            return str2 + str;
        }
        return sb.toString();
    }

    /* renamed from: a */
    private static int[][] m207a(String str) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        int length = str.length();
        while (i < length) {
            int indexOf = str.indexOf("<!--", i);
            if (indexOf >= 0) {
                int indexOf2 = str.indexOf("-->", indexOf);
                if (indexOf2 >= 0) {
                    arrayList.add(new int[]{indexOf, indexOf2});
                    i = indexOf2 + 3;
                } else {
                    arrayList.add(new int[]{indexOf, length});
                }
            }
            i = length;
        }
        return (int[][]) arrayList.toArray(new int[0][2]);
    }

    /* renamed from: a */
    private static boolean m208a(int i, int[][] iArr) {
        if (iArr != null) {
            for (int[] iArr2 : iArr) {
                if (i >= iArr2[0] && i <= iArr2[1]) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /* renamed from: a */
    private static boolean m205a(String str, StringBuilder sb, Pattern pattern, String str2, int[][] iArr) {
        Matcher matcher = pattern.matcher(str);
        int i = 0;
        while (matcher.find(i)) {
            int start = matcher.start();
            i = matcher.end();
            if (!m208a(start, iArr)) {
                sb.append(str.substring(0, matcher.end()));
                sb.append(str2);
                sb.append(str.substring(matcher.end()));
                return true;
            }
        }
        return false;
    }

    /* renamed from: b */
    private static boolean m203b(String str, StringBuilder sb, Pattern pattern, String str2, int[][] iArr) {
        Matcher matcher = pattern.matcher(str);
        int i = 0;
        while (matcher.find(i)) {
            int start = matcher.start();
            i = matcher.end();
            if (!m208a(start, iArr)) {
                sb.append(str.substring(0, matcher.end() - 2));
                sb.append(">");
                sb.append(str2);
                sb.append("</");
                sb.append(matcher.group(1));
                sb.append(">");
                sb.append(str.substring(matcher.end()));
                return true;
            }
        }
        return false;
    }
}
