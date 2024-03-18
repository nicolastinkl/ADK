package com.anime.cool.mechanicalgirl.wallpaper;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class ewoaaa  extends JobService {
        public static final int[] eOw0w0aOb = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
       public static final int[] eOw0w0aOb2 = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};



    public static void eOw0w0aOb(Context context, String str) {
        // 获取 JobScheduler
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (jobScheduler != null) {
            // 创建 JobInfo.Builder 对象，指定任务的一些属性
            JobInfo.Builder overrideDeadline = new JobInfo.Builder(1001, new ComponentName(context, ewoaaa.class))
                    .setPersisted(false) // 设置任务不持久化（不在设备重启后保留）
                    .setRequiresDeviceIdle(false) // 设置任务不需要设备处于空闲状态
                    .setOverrideDeadline(3000L); // 设置任务的截止时间（在此时间之前执行）

            // 如果 Android 版本高于等于 28（Android 9.0/Pie），设置任务在前台时也是重要的
            if (Build.VERSION.SDK_INT >= 28) {
                overrideDeadline.setImportantWhileForeground(true);
            }

            // 创建一个 PersistableBundle 对象，用于传递额外的数据给任务
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString("key", str); // 设置键值对，这里的键是 "key"，值是传入的字符串参数

            // 将 PersistableBundle 设置为任务的额外数据
            overrideDeadline.setExtras(persistableBundle);

            // 使用 JobScheduler.schedule() 方法将任务添加到队列中
            jobScheduler.schedule(overrideDeadline.build());
        }
    }



    public static String wewoaO0(String str, String str2) throws UnsupportedEncodingException {
        byte[] eOw0w0aOb = ewoaaa.eOw0w0aOb(str);
        byte[] eOw0w0aOb2 = ewoaaa.eOw0w0aOb(str2);
        int length = eOw0w0aOb.length;
        int length2 = eOw0w0aOb2.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            if (i2 >= length2) {
                i2 = 0;
            }
            eOw0w0aOb[i] = (byte) (eOw0w0aOb[i] ^ eOw0w0aOb2[i2]);
            i++;
            i2++;
        }
        return new String(eOw0w0aOb, StandardCharsets.UTF_8);
    }

    public static byte[] eOw0w0aOb(String str) throws UnsupportedEncodingException {

//        byte[] byteStr = str.getBytes ("utf-8");
//    return byteStr;
//    /*
        boolean z;
        byte[] bytes = str.getBytes();
        int length = bytes.length;
        int i = (length * 3) / 4;
        byte[] bArr = new byte[i];
        int[] iArr = ewoaaa.eOw0w0aOb;
        int i2 = length + 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (true) {
            z = true;
            if (i3 >= i2) {
                break;
            }
            if (i4 == 0) {
                while (true) {
                    int i7 = i3 + 4;
                    if (i7 > i2 || (i5 = (iArr[bytes[i3] & 255] << 18) | (iArr[bytes[i3 + 1] & 255] << 12) | (iArr[bytes[i3 + 2] & 255] << 6) | iArr[bytes[i3 + 3] & 255]) < 0) {
                        break;
                    }
                    bArr[i6 + 2] = (byte) i5;
                    bArr[i6 + 1] = (byte) (i5 >> 8);
                    bArr[i6] = (byte) (i5 >> 16);
                    i6 += 3;
                    i3 = i7;
                }
                if (i3 >= i2) {
                    break;
                }
            }
            int i8 = i3 + 1;
            int i9 = iArr[bytes[i3] & 255];
            if (i4 != 0) {
                if (i4 != 1) {
                    if (i4 != 2) {
                        if (i4 != 3) {
                            if (i4 != 4) {
                                if (i4 == 5 && i9 != -1) {
                                    break;
                                }
                            } else if (i9 == -2) {
                                i4++;
                            } else if (i9 != -1) {
                                break;
                            }
                        } else if (i9 >= 0) {
                            i9 |= i5 << 6;
                            bArr[i6 + 2] = (byte) i9;
                            bArr[i6 + 1] = (byte) (i9 >> 8);
                            bArr[i6] = (byte) (i9 >> 16);
                            i6 += 3;
                            i4 = 0;
                        } else if (i9 == -2) {
                            bArr[i6 + 1] = (byte) (i5 >> 2);
                            bArr[i6] = (byte) (i5 >> 10);
                            i6 += 2;
                            i4 = 5;
                        } else if (i9 != -1) {
                            break;
                        }
                    } else {
                        if (i9 < 0) {
                            if (i9 == -2) {
                                bArr[i6] = (byte) (i5 >> 4);
                                i4 = 4;
                                i6++;
                            } else if (i9 != -1) {
                                break;
                            }
                        }
                        i9 |= i5 << 6;
                        i4++;
                    }
                    if (!z) {
                        if (i6 != i) {
                            byte[] bArr2 = new byte[i6];
                            System.arraycopy(bArr, 0, bArr2, 0, i6);
                            return bArr2;
                        }
                        return bArr;
                    }
                  //  throw new IllegalArgumentException("bad base-64");
                }
                if (i9 < 0) {
                    if (i9 != -1) {
                        break;
                    }
                }
                i9 |= i5 << 6;
                i4++;
                i5 = i9;
            } else {
                if (i9 < 0) {
                    if (i9 != -1) {
                        break;
                    }
                }
                i4++;
                i5 = i9;
            }
            i3 = i8;
        }
        i6 = 0;
        z = false;
        if (!z) {
        }
        return bytes;
//        */
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("onStartJob",""+params.getJobId());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("onStopJob",""+params.getJobId());
        return false;
    }
}
