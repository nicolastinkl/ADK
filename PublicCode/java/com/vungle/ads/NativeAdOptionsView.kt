package com.vungle.ads

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.vungle.ads.internal.util.ViewUtility.dpToPixels

internal class NativeAdOptionsView : FrameLayout {

    private lateinit var icon: ImageView

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        icon = ImageView(context)
        val size = dpToPixels(context, AD_OPTIONS_VIEW_SIZE)
        val params = LayoutParams(size, size)
        icon.layoutParams = params
        addView(icon)
    }

    internal fun getPrivacyIcon() = icon

    fun renderTo(rootView: FrameLayout, @NativeAd.AdOptionsPosition optionsPosition: Int) {
        if (parent != null) {
            (parent as ViewGroup).removeView(this)
        }
        rootView.addView(this)
        val size = dpToPixels(context, AD_OPTIONS_VIEW_SIZE)
        val params = LayoutParams(size, size)
        when (optionsPosition) {
            NativeAd.TOP_LEFT -> params.gravity = Gravity.TOP or Gravity.START
            NativeAd.BOTTOM_LEFT -> params.gravity = Gravity.BOTTOM or Gravity.START
            NativeAd.BOTTOM_RIGHT -> params.gravity = Gravity.BOTTOM or Gravity.END
            NativeAd.TOP_RIGHT -> params.gravity = Gravity.TOP or Gravity.END
            else -> params.gravity = Gravity.TOP or Gravity.END
        }
        layoutParams = params
        rootView.requestLayout()
    }

    fun destroy() {
        removeAllViews()
        if (parent != null) {
            (parent as ViewGroup).removeView(this)
        }
    }

    companion object {
        private const val AD_OPTIONS_VIEW_SIZE = 20
    }
}
