package com.fkdeepal.tools.ext.adapter.base

import android.content.Context
import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseViewHolder<VB : ViewBinding>(val mContext: Context, val mViewBinding: VB) : RecyclerView.ViewHolder(mViewBinding.root) {
    protected val mResources: Resources

    init {
        mResources = mContext.resources

    }


}