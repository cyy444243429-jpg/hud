package com.fkdeepal.tools.ext.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val horizontalSpaceWidth: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0
        
        // 第一个项左边距为0，其他项左边距为固定值
        if (position > 0) {
            outRect.left = horizontalSpaceWidth
        }
        
        // 最后一个项右边距为0
        if (position == itemCount - 1) {
            outRect.right = 0
        }
        
        // 上下边距为0，保持紧凑
        outRect.top = 0
        outRect.bottom = 0
    }
}
