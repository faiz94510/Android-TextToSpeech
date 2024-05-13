package com.example.texttospeech.main.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView


class MyRecycler : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var isVerticalScrollingEnabled = false
        private set

    fun enableVerticalScroll(enabled: Boolean) {
        isVerticalScrollingEnabled = enabled
    }

    override fun computeVerticalScrollRange(): Int {
        return if (isVerticalScrollingEnabled) super.computeVerticalScrollRange() else 0
    } //    @Override
    //    public boolean onInterceptTouchEvent(MotionEvent e) {
    //        if (isVerticalScrollingEnabled())
    //            return super.onInterceptTouchEvent(e);
    //        return false;
    //    }
}
