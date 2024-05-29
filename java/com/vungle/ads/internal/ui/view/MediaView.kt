package com.vungle.ads.internal.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout

/**
 * Display native ad media content.
 */
class MediaView : RelativeLayout {
    private lateinit var imageView: ImageView

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
        imageView = ImageView(context)
        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        params.addRule(CENTER_IN_PARENT)
        imageView.layoutParams = params
        imageView.adjustViewBounds = true
        addView(imageView)
        requestLayout()
    }

    internal fun getMainImage() = imageView

    fun destroy() {
        imageView.setImageDrawable(null)
        if (imageView.parent != null) {
            (imageView.parent as ViewGroup).removeView(imageView)
        }
    }
}