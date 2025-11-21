package com.fkdeepal.tools.ext.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.fkdeepal.tools.ext.R
import com.fkdeepal.tools.ext.adapter.base.BaseAdapter
import com.fkdeepal.tools.ext.bean.AmapDriveWayInfoBean
import com.fkdeepal.tools.ext.databinding.ItemHubDriveWayBinding
import com.fkdeepal.tools.ext.utils.AppUtils
import com.fkdeepal.tools.ext.utils.SvgLoader
import com.fkdeepal.tools.ext.utils.PreferenceUtils
import com.fkdeepal.tools.ext.utils.ColorPreferenceManager
import com.jeremyliao.liveeventbus.LiveEventBus
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter

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
    
    init {
        // 监听颜色变化
        setupColorChangeListener()
        Timber.tag(TAG).d("HudAmapDriveWayAdapter初始化，数据数量: ${mData.size}")
    }

    override fun onCreateViewBinding(layoutInflater: LayoutInflater,
                                     parent: ViewGroup,
                                     viewType: Int): ItemHubDriveWayBinding {
        (AppUtils.appContext.applicationContext as? com.fkdeepal.tools.ext.app.AppApplication)?.logOperation(
            "HudAmapDriveWayAdapter.onCreateViewBinding()"
        )
        Timber.tag(TAG).d("创建视图绑定")
        return ItemHubDriveWayBinding.inflate(layoutInflater, parent, false)
    }

    private fun setupColorChangeListener() {
        // 监听颜色更新事件
        LiveEventBus.get("land_color_update", Boolean::class.java)
            .observeForever {
                Timber.tag(TAG).d("收到颜色更新事件，刷新所有可见图标")
                // 强制刷新所有可见的图标
                notifyDataSetChanged()
            }
    }

    fun setImageDrawable(viewBinding: ItemHubDriveWayBinding, resourceName: String,
                         fail: () -> Unit) {
        
        runCatching {
            val resourceId = mResources.getIdentifier(resourceName, "drawable", mPackageName)
            var drawable = ContextCompat.getDrawable(mContext, resourceId)
            if (drawable != null) {
                // 应用实时颜色
                applyColorToDrawable(drawable, resourceName)
                viewBinding.ivIcon.setImageDrawable(drawable)
                
                // 动态设置图标尺寸
                val iconHeight = PreferenceUtils.getLandIconSize(mContext)
                val iconWidth = PreferenceUtils.getLandIconWidth(mContext)
                
                val layoutParams = viewBinding.ivIcon.layoutParams
                layoutParams.width = iconWidth
                layoutParams.height = iconHeight
                viewBinding.ivIcon.layoutParams = layoutParams
                
                Timber.tag(TAG).d("成功加载并着色图标: $resourceName, 尺寸: ${iconWidth}x${iconHeight}px")
            } else {
                viewBinding.ivIcon.setImageDrawable(null)
                Timber.tag(TAG).w("图标加载为null: $resourceName")
            }
        }.onFailure {
            Timber.tag(TAG).e(it, "加载图标失败: $resourceName")
            viewBinding.ivIcon.setImageDrawable(null)
            fail.invoke()
        }
    }
    
    private fun applyColorToDrawable(drawable: android.graphics.drawable.Drawable, resourceName: String) {
        val iconNumber = extractIconNumber(resourceName)
        val primaryColor = ColorPreferenceManager.getPrimaryColor()
        val secondaryColor = ColorPreferenceManager.getSecondaryColor()
        
        Timber.tag(TAG).d("应用颜色到图标 - 资源: $resourceName, 编号: $iconNumber, 主色: ${String.format("#%06X", 0xFFFFFF and primaryColor)}, 次色: ${String.format("#%06X", 0xFFFFFF and secondaryColor)}")
        
        // 根据图标编号应用颜色策略
        when {
            iconNumber in 0..14 -> {
                drawable.setTint(primaryColor)
                Timber.tag(TAG).d("应用主颜色到图标 $resourceName")
            }
            iconNumber in 15..29 -> {
                drawable.setTint(secondaryColor)
                Timber.tag(TAG).d("应用次颜色到图标 $resourceName")
            }
            iconNumber in 30..48 -> {
                drawable.setTint(primaryColor)
                Timber.tag(TAG).d("应用主颜色到多箭头图标 $resourceName")
            }
            iconNumber in 49..55 -> {
                drawable.setTint(primaryColor)
                Timber.tag(TAG).d("应用主颜色到图标 $resourceName")
            }
            iconNumber in 56..70 -> {
                drawable.setTint(secondaryColor)
                Timber.tag(TAG).d("应用次颜色到图标 $resourceName")
            }
            iconNumber in 71..83 -> {
                drawable.setTint(primaryColor)
                Timber.tag(TAG).d("应用主颜色到多箭头图标 $resourceName")
            }
            else -> {
                drawable.setTint(primaryColor)
                Timber.tag(TAG).d("应用默认主颜色到图标 $resourceName")
            }
        }
    }
    
    private fun extractIconNumber(resourceName: String): Int {
        return try {
            resourceName.replace("ic_land_", "").toInt()
        } catch (e: Exception) {
            89 // 默认
        }
    }

    override fun setViewHolderData(viewBinding: ItemHubDriveWayBinding,
                                   item: AmapDriveWayInfoBean,
                                   position: Int) {
        // 记录操作
        (AppUtils.appContext.applicationContext as? com.fkdeepal.tools.ext.app.AppApplication)?.logOperation(
            "HudAmapDriveWayAdapter.setViewHolderData() - pos: $position, icon: ${item.drive_way_lane_Back_icon}"
        )
        
        runCatching {
            val icon = item.drive_way_lane_Back_icon
            Timber.tag(TAG).d("设置车道数据 - 位置: $position, 图标: $icon")
            
            viewBinding.tvValue.visibility = View.GONE
            
            if (icon.isNullOrBlank()) {
                // 使用安全的默认资源加载
                runCatching {
                    val resourceId = mResources.getIdentifier("ic_land_89", "drawable", mPackageName)
                    val drawable = ContextCompat.getDrawable(mContext, resourceId)
                    if (drawable != null) {
                        // 应用颜色到默认图标
                        applyColorToDrawable(drawable, "ic_land_89")
                        viewBinding.ivIcon.setImageDrawable(drawable)
                        
                        // 动态设置默认图标尺寸
                        val iconHeight = PreferenceUtils.getLandIconSize(mContext)
                        val iconWidth = PreferenceUtils.getLandIconWidth(mContext)
                        
                        val layoutParams = viewBinding.ivIcon.layoutParams
                        layoutParams.width = iconWidth
                        layoutParams.height = iconHeight
                        viewBinding.ivIcon.layoutParams = layoutParams
                        
                        Timber.tag(TAG).d("使用默认图标并应用颜色 - 位置: $position, 尺寸: ${iconWidth}x${iconHeight}px")
                    } else {
                        viewBinding.ivIcon.setImageDrawable(null)
                        Timber.tag(TAG).w("默认图标加载失败 - 位置: $position")
                    }
                }.onFailure {
                    Timber.tag(TAG).e(it, "加载默认图标失败 - 位置: $position")
                    viewBinding.ivIcon.setImageDrawable(null)
                }
            } else {
                // 完整的 ic_land_xx 图标加载
                val resourceName = "ic_land_${item.drive_way_lane_Back_icon}"
                Timber.tag(TAG).d("加载图标: $resourceName - 位置: $position")
                setImageDrawable(viewBinding, resourceName) {
                    Timber.tag(TAG).d("图标加载失败，显示文本: $icon")
                    viewBinding.tvValue.visibility = View.VISIBLE
                    viewBinding.tvValue.text = icon
                }
            }
            
        }.onFailure { exception ->
            Timber.tag(TAG).e(exception, "设置车道数据时发生异常 - 位置: $position")
            // 记录详细的异常信息
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            exception.printStackTrace(pw)
            Timber.tag(TAG).e("异常堆栈:\n${sw.toString()}")
            
            // 异常情况下设置透明背景
            viewBinding.ivIcon.setImageDrawable(null)
        }
    }
    
    /**
     * 刷新所有图标尺寸（用于实时更新）
     */
    fun refreshIconSizes() {
        Timber.tag(TAG).d("刷新所有图标尺寸")
        notifyDataSetChanged()
    }
    
    /**
     * 新增：刷新所有图标颜色（用于实时更新）
     */
    fun refreshIconColors() {
        Timber.tag(TAG).d("刷新所有图标颜色")
        notifyDataSetChanged()
    }
}
