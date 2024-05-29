package com.vungle.androidplugin;


/* loaded from: vunglePlugin.aar:classes.jar:com/vungle/androidplugin/IVungleFeed.class */
public interface IVungleFeed {
    int getSize();

    void setPosition(int i);

    void setOffset(int i, int i2);

    void adjustOffset();

    void pause();

    void resume();
}