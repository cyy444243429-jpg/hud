package com.fkdeepal.tools.ext.adapter

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.fkdeepal.tools.ext.adapter.base.BaseAdapter
import com.fkdeepal.tools.ext.databinding.ItemLogFileListBinding
import com.fkdeepal.tools.ext.utils.AppUtils
import java.io.File

class LogFileListAdapter(mData: ArrayList<File>,
                         mOnItemClickListener: ((File, Int) -> Unit)? = null,
                         mOnItemLongClickListener: ((File, Int) -> Unit)? = null)
    : BaseAdapter<ItemLogFileListBinding, File>(AppUtils.appContext,
                                                mData, mOnItemClickListener, mOnItemLongClickListener) {
    private val mResources by lazy {
        mContext.getResources()
    }
    private val mPackageName by lazy {
        mContext.getPackageName()
    }

    override fun onCreateViewBinding(layoutInflater: LayoutInflater,
                                     parent: ViewGroup,
                                     viewType: Int): ItemLogFileListBinding {
        return ItemLogFileListBinding.inflate(layoutInflater, parent, false)
    }


    override fun setViewHolderData(viewBinding: ItemLogFileListBinding,
                                   item: File,
                                   position: Int) {
        viewBinding.tvName.text = item.name ?: ""
        viewBinding.tvSize.text = Formatter.formatFileSize(mContext, item.length())

    }
}