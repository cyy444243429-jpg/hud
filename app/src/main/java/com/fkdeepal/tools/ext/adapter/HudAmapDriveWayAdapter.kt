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
        
        // 添加内存监控
        logMemoryStatus("开始加载图标: $resourceName")

        runCatching {
            // 提取图标编号 (从 "ic_land_66" 中提取 "66")
            val iconNumber = resourceName.removePrefix("ic_land_")
            Timber.tag(TAG).d("加载车道图标, 资源名: $resourceName, 编号: $iconNumber")
            
            // 使用SVG加载器加载图标
            val drawable = SvgLoader.loadLandIcon(mContext, iconNumber)
            if (drawable != null) {
                // 在主线程安全设置图片
                viewBinding.root.post {
                    runCatching {
                        // 应用实时颜色到SVG图标
                        applyColorToDrawable(drawable, resourceName)
                        viewBinding.ivIcon.setImageDrawable(drawable)
                        
                        // 动态设置图标尺寸
                        val iconHeight = PreferenceUtils.getLandIconSize(mContext)
                        val iconWidth = PreferenceUtils.getLandIconWidth(mContext)
                        
                        val layoutParams = viewBinding.ivIcon.layoutParams
                        layoutParams.width = iconWidth
                        layoutParams.height = iconHeight
                        viewBinding.ivIcon.layoutParams = layoutParams
                        
                        Timber.tag(TAG).d("成功加载车道图标: $resourceName, 尺寸: ${iconWidth}x${iconHeight}px")
                        logMemoryStatus("车道图标加载完成: $resourceName")
                        
                    }.onFailure { e ->
                        Timber.tag(TAG).e(e, "设置车道图标时发生错误: $resourceName")
                        // 加载失败时设置透明背景
                        viewBinding.ivIcon.setImageDrawable(null)
                        fail.invoke()
                    }
                }
            } else {
                Timber.tag(TAG).w("车道图标加载为null: $resourceName")
                viewBinding.root.post {
                    viewBinding.ivIcon.setImageDrawable(null)
                    fail.invoke()
                }
            }
        }.onFailure { exception ->
            Timber.tag(TAG).e(exception, "加载车道图标失败: $resourceName")
            viewBinding.root.post {
                viewBinding.ivIcon.setImageDrawable(null)
                fail.invoke()
            }
        }
    }
    
    // ========== 修改：应用颜色到drawable，支持高亮显示 ==========
    private fun applyColorToDrawable(drawable: android.graphics.drawable.Drawable, resourceName: String) {
        val iconNumber = extractIconNumber(resourceName)
        
        val color = if (ColorPreferenceManager.isHighlightActive()) {
            // 高亮状态：使用跑马灯颜色
            val highlightColor = ColorPreferenceManager.getHighlightColor()
            Timber.tag(TAG).d("应用高亮颜色到图标: $resourceName, 颜色: ${String.format("#%06X", 0xFFFFFF and highlightColor)}")
            highlightColor
        } else {
            // 正常状态：使用原有颜色逻辑
            val primaryColor = ColorPreferenceManager.getPrimaryColor()
            val secondaryColor = ColorPreferenceManager.getSecondaryColor()
            
            val normalColor = when {
                iconNumber in 0..14 -> primaryColor
                iconNumber in 15..29 -> secondaryColor
                iconNumber in 30..48 -> primaryColor
                iconNumber in 49..55 -> primaryColor
                iconNumber in 56..70 -> secondaryColor
                iconNumber in 71..83 -> primaryColor
                else -> primaryColor
            }
            Timber.tag(TAG).d("应用正常颜色到图标: $resourceName, 颜色: ${String.format("#%06X", 0xFFFFFF and normalColor)}")
            normalColor
        }
        
        drawable.setTint(color)
    }
    
    // ========== 新增：提取图标编号 ==========
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
        
        // 添加内存监控
        logMemoryStatus("开始设置车道数据 - 位置: $position")
        
        runCatching {
            val icon = item.drive_way_lane_Back_icon
            Timber.tag(TAG).d("设置车道数据 - 位置: $position, 图标: $icon")
            
            viewBinding.tvValue.visibility = View.GONE
            
            if (icon.isNullOrBlank()) {
                // 使用安全的默认资源加载
                runCatching {
                    val drawable = SvgLoader.loadLandIcon(mContext, "89")
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
                        
                        Timber.tag(TAG).d("使用默认 SVG 图标 - 位置: $position, 尺寸: ${iconWidth}x${iconHeight}px")
                    } else {
                        viewBinding.ivIcon.setImageDrawable(null)
                        Timber.tag(TAG).w("默认 SVG 图标加载失败 - 位置: $position")
                    }
                    logMemoryStatus("默认 SVG 图标设置完成 - 位置: $position")
                }.onFailure {
                    Timber.tag(TAG).e(it, "加载默认 SVG 图标失败 - 位置: $position")
                    viewBinding.ivIcon.setImageDrawable(null)
                }
            } else {
                // 完整的 ic_land_xx 图标加载
                val resourceName = "ic_land_${item.drive_way_lane_Back_icon}"
                Timber.tag(TAG).d("加载 SVG 图标: $resourceName - 位置: $position")
                setImageDrawable(viewBinding, resourceName) {
                    Timber.tag(TAG).d("SVG 图标加载失败，显示文本: $icon")
                    viewBinding.tvValue.visibility = View.VISIBLE
                    viewBinding.tvValue.text = icon
                }
            }
            
            logMemoryStatus("车道数据设置完成 - 位置: $position")
            
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
    
    private fun logMemoryStatus(operation: String) {
        try {
            val runtime = Runtime.getRuntime()
            val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
            val maxMemory = runtime.maxMemory() / (1024 * 1024)
            Timber.tag(TAG).d("$operation - 内存: ${usedMemory}MB/${maxMemory}MB")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "获取内存状态失败")
        }
    }
    
    /**
     * 清理适配器相关的缓存
     */
    fun clearAdapterCache() {
        Timber.tag(TAG).d("清理适配器缓存")
        SvgLoader.clearCache()
    }
    
    /**
     * 获取缓存统计信息
     */
    fun getCacheStatistics(): String {
        return SvgLoader.getCacheStats()
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
