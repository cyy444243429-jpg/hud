package com.fkdeepal.tools.ext.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.fkdeepal.tools.ext.R
import com.fkdeepal.tools.ext.adapter.base.BaseAdapter
import com.fkdeepal.tools.ext.bean.AmapDriveWayInfoBean
import com.fkdeepal.tools.ext.databinding.ItemHubDriveWayBinding
import com.fkdeepal.tools.ext.utils.AppUtils

class HudAmapDriveWayAdapter(mData: ArrayList<AmapDriveWayInfoBean>) : BaseAdapter<ItemHubDriveWayBinding, AmapDriveWayInfoBean>(AppUtils.appContext,
                                                                                                                                 mData) {
    private val mResources by lazy {
       mContext.resources
    }
    private val mPackageName  by lazy {
        mContext.packageName
    }
    
    companion object {
        private const val TAG = "HudAmapDriveWayAdapter"
    }

    override fun onCreateViewBinding(layoutInflater: LayoutInflater,
                                     parent: ViewGroup,
                                     viewType: Int): ItemHubDriveWayBinding {
        return ItemHubDriveWayBinding.inflate(layoutInflater, parent, false)
    }

    fun setImageDrawable(viewBinding: ItemHubDriveWayBinding, resourceName: String,
                         fail: () -> Unit) {

        runCatching {
            // 使用更安全的矢量图加载方式
            val resourceId = mResources.getIdentifier(resourceName, "drawable", mPackageName)
            if (resourceId != 0) {
                // 使用 AppCompatResources 加载矢量图
                val drawable = AppCompatResources.getDrawable(mContext, resourceId)
                if (drawable != null) {
                    // 在主线程安全设置图片
                    viewBinding.root.post {
                        viewBinding.ivIcon.setImageDrawable(drawable)
                    }
                } else {
                    Log.w(TAG, "矢量图加载为null: $resourceName")
                    viewBinding.root.post {
                        viewBinding.ivIcon.setImageDrawable(null)
                        fail.invoke()
                    }
                }
            } else {
                Log.w(TAG, "未找到资源: $resourceName")
                viewBinding.root.post {
                    viewBinding.ivIcon.setImageDrawable(null)
                    fail.invoke()
                }
            }
        }.onFailure { exception ->
            Log.e(TAG, "加载矢量图失败: $resourceName", exception)
            viewBinding.root.post {
                viewBinding.ivIcon.setImageDrawable(null)
                fail.invoke()
            }
        }
    }

    override fun setViewHolderData(viewBinding: ItemHubDriveWayBinding,
                                   item: AmapDriveWayInfoBean,
                                   position: Int) {
        val icon = item.drive_way_lane_Back_icon
        viewBinding.tvValue.visibility = View.GONE
        if (icon.isNullOrBlank()) {
            // 使用安全的默认资源加载
            runCatching {
                val drawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_land_89)
                viewBinding.ivIcon.setImageDrawable(drawable)
            }.onFailure {
                viewBinding.ivIcon.setImageDrawable(null)
            }
        } else {
            val resourceName = "ic_land_${item.drive_way_lane_Back_icon}"
            setImageDrawable(viewBinding, resourceName) {
                viewBinding.tvValue.visibility = View.VISIBLE
                viewBinding.tvValue.text = icon
            }
        }
    }
}
