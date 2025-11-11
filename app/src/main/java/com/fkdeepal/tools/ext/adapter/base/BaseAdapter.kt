package com.fkdeepal.tools.ext.adapter.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<VB : ViewBinding, T>(val mContext: Context,
                                                val mData: ArrayList<T>,
                                                val mOnItemClickListener: ((T, Int) -> Unit)? = null,
                                                val mOnItemLongClickListener: ((T, Int) -> Unit)? = null) :
        RecyclerView.Adapter<BaseViewHolder<VB>>() {

    val layoutInflater: LayoutInflater
    protected val TAG by  lazy { this.javaClass.simpleName }
    init {
        layoutInflater = LayoutInflater.from(mContext)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    fun cleanAndAddNewData(data: List<T>?){
        mData.clear()
        if (data!=null){
            mData.addAll(data)
        }
        notifyDataSetChanged()
    }
    abstract fun onCreateViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): VB
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {

        val viewBinding = onCreateViewBinding(layoutInflater, parent, viewType)
        val viewHolder = BaseViewHolder(mContext, viewBinding)
        if (mOnItemClickListener != null) {
            viewHolder.mViewBinding.root.setOnClickListener {
                val position = viewHolder.adapterPosition
                mData.getOrNull(position)
                    ?.let {
                        mOnItemClickListener.invoke(it, position)
                    }
            }

        }
        if (mOnItemLongClickListener != null) {
            viewHolder.mViewBinding.root.setOnLongClickListener {
                val position = viewHolder.adapterPosition
                mData.getOrNull(position)
                    ?.let {
                        mOnItemLongClickListener.invoke(it, position)
                    }
                true
            }
        }
        onBindViewHolderListener(viewHolder)
        return viewHolder
    }

    open fun onBindViewHolderListener(viewHolder: BaseViewHolder<VB>){

    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val item = mData.getOrNull(position)
        if (item != null) {
            setViewHolderData(holder.mViewBinding, item, position)
        }
    }

    abstract fun setViewHolderData(viewBinding: VB, item: T, position: Int)
}