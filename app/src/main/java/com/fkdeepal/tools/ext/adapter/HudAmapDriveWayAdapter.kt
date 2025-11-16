package com.fkdeepal.tools.ext.adapter

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

class HudAmapDriveWayAdapter(mData: ArrayList<AmapDriveWayInfoBean>) : BaseAdapter<ItemHubDriveWayBinding, AmapDriveWayInfoBean>(
    AppUtils.appContext,
    mData
) {
    private val mResources by lazy {
        mContext.resources
    }
    private val mPackageName by lazy {
        mContext.packageName
    }

    override fun onCreateViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemHubDriveWayBinding {
        return ItemHubDriveWayBinding.inflate(layoutInflater, parent, false)
    }

    fun setImageDrawable(viewBinding: ItemHubDriveWayBinding, resourceName: String,
                         fail: () -> Unit) {

        runCatching {
            // 修改：查找xml资源而不是png，使用AppCompatResources以支持矢量图
            val resourceId = mResources.getIdentifier(resourceName, "drawable", mPackageName)
            if (resourceId != 0) {
                val drawable = AppCompatResources.getDrawable(mContext, resourceId)
                if (drawable != null) {
                    viewBinding.ivIcon.setImageDrawable(drawable)
                } else {
                    viewBinding.ivIcon.setImageDrawable(null)
                    fail.invoke()
                }
            } else {
                viewBinding.ivIcon.setImageDrawable(null)
                fail.invoke()
            }
        }.onFailure {
            viewBinding.ivIcon.setImageDrawable(null)
            fail.invoke()
        }
    }

    override fun setViewHolderData(
        viewBinding: ItemHubDriveWayBinding,
        item: AmapDriveWayInfoBean,
        position: Int
    ) {
        val icon = item.drive_way_lane_Back_icon
        viewBinding.tvValue.visibility = View.GONE
        if (icon.isNullOrBlank()) {
            // 修改：使用xml资源而不是png
            viewBinding.ivIcon.setImageResource(R.drawable.ic_land_89)
        } else {
            // 注意：资源名称不变，仍然是 "ic_land_xx"，但文件扩展名已改为.xml
            val resourceName = "ic_land_${item.drive_way_lane_Back_icon}"
            setImageDrawable(viewBinding, resourceName) {
                viewBinding.tvValue.visibility = View.VISIBLE
                viewBinding.tvValue.text = icon
            }
        }
    }
}
