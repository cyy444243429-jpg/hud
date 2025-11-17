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
import timber.log.Timber

class HudAmapDriveWayAdapter(mData: ArrayList<AmapDriveWayInfoBean>) : BaseAdapter<ItemHubDriveWayBinding, AmapDriveWayInfoBean>(
    AppUtils.appContext,
    mData
) {
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
                    Timber.tag(TAG).d("成功加载矢量图: $resourceName")
                } else {
                    Timber.tag(TAG).w("矢量图加载为null: $resourceName")
                    viewBinding.root.post {
                        viewBinding.ivIcon.setImageDrawable(null)
                        fail.invoke()
                    }
                }
            } else {
                Timber.tag(TAG).w("未找到资源: $resourceName")
                viewBinding.root.post {
                    viewBinding.ivIcon.setImageDrawable(null)
                    fail.invoke()
                }
            }
        }.onFailure { exception ->
            Timber.tag(TAG).e(exception, "加载矢量图失败: $resourceName")
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
                Timber.tag(TAG).d("使用默认图标")
            }.onFailure {
                Timber.tag(TAG).e(it, "加载默认图标失败")
                viewBinding.ivIcon.setImageDrawable(null)
            }
        } else {
            // ========== 临时注释掉 ic_land_xx 图标加载 ==========
            Timber.tag(TAG).d("跳过ic_land_xx图标加载 - 位置: $position, 图标: $icon (测试模式)")
            
            // 直接显示文本，不加载ic_land_xx图标
            viewBinding.tvValue.visibility = View.VISIBLE
            viewBinding.tvValue.text = icon
            viewBinding.ivIcon.visibility = View.GONE
            
            /*
            // ========== 原有的 ic_land_xx 图标加载代码 ==========
            val resourceName = "ic_land_${item.drive_way_lane_Back_icon}"
            setImageDrawable(viewBinding, resourceName) {
                Timber.tag(TAG).d("图标加载失败，显示文本: $icon")
                viewBinding.tvValue.visibility = View.VISIBLE
                viewBinding.tvValue.text = icon
            }
            */
            // ========== 注释结束 ==========
        }
    }
}
