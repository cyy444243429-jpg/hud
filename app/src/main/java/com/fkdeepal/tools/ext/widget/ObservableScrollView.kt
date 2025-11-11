package com.fkdeepal.tools.ext.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class ObservableScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    interface OnScrollChangedListener {
        fun onScrollBottom() // 滚动到底部
        fun onScrollTop()    // 滚动到顶部
        fun onScrollChanged(scrollY: Int) // 滚动变化
    }

    private var scrollListener: OnScrollChangedListener? = null

    fun setOnScrollChangedListener(listener: OnScrollChangedListener) {
        this.scrollListener = listener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        scrollListener?.onScrollChanged(t)

        // 检查是否滚动到底部
        val view = getChildAt(0)
        if (view != null && t + height >= view.height - 10) { // 10像素容差
            scrollListener?.onScrollBottom()
        }

        // 检查是否滚动到顶部
        if (t == 0) {
            scrollListener?.onScrollTop()
        }
    }
}