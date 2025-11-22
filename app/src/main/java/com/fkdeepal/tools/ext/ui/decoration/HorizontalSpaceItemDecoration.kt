package com.fkdeepal.tools.ext.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class HorizontalSpaceItemDecoration(private val horizontalSpaceWidth: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0
        
        Timber.d("HorizontalSpaceItemDecoration - 位置: $position, 总数: $itemCount, 间距: ${horizontalSpaceWidth}px")
        
        // 使用负间距让图标重叠
        if (position > 0) {
            outRect.left = horizontalSpaceWidth
        } else {
            outRect.left = 0
        }
        
        // 所有项右边距都为0
        outRect.right = 0
        
        // 上下边距为0，保持紧凑
        outRect.top = 0
        outRect.bottom = 0
        
        Timber.d("HorizontalSpaceItemDecoration - 设置边距: left=${outRect.left}, right=${outRect.right}")
    }
}
