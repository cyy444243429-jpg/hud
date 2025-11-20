package com.fkdeepal.tools.ext.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.fkdeepal.tools.ext.R
import com.fkdeepal.tools.ext.adapter.base.BaseAdapter
import com.fkdeepal.tools.ext.bean.AmapDriveWayInfoBean
import com.fkdeepal.tools.ext.databinding.ItemHubDriveWayBinding
import com.fkdeepal.tools.ext.utils.AppUtils
import com.fkdeepal.tools.ext.utils.SvgLoader
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
        // 新的图标尺寸常量
        private const val ICON_WIDTH = 40
        private const val ICON_HEIGHT = 55
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

    fun setImageDrawable(viewBinding: ItemHubDriveWayBinding, resourceName: String,
                         fail: () -> Unit) {
        
        // 添加内存监控
        logMemoryStatus("开始加载图标: $resourceName")

        runCatching {
            // 提取图标编号 (从 "ic_land_66" 中提取 "66")
            val iconNumber = resourceName.removePrefix("ic_land_")
            Timber.tag(TAG).d("加载车道图标, 资源名: $resourceName, 编号: $iconNumber")
            
            // 从 raw 资源加载 SVG
            val resourceId = mResources.getIdentifier(resourceName, "raw", mPackageName)
            if (resourceId != 0) {
                val drawable = VectorDrawableCompat.create(mResources, resourceId, null)
                if (drawable != null) {
                    // 关键：设置正确的固有尺寸
                    drawable.setBounds(0, 0, ICON_WIDTH, ICON_HEIGHT)
                    
                    // 在主线程安全设置图片
                    viewBinding.root.post {
                        runCatching {
                            viewBinding.ivIcon.setImageDrawable(drawable)
                            
                            // 确保 ImageView 尺寸正确
                            val layoutParams = viewBinding.ivIcon.layoutParams
                            layoutParams.width = ICON_WIDTH
                            layoutParams.height = ICON_HEIGHT
                            viewBinding.ivIcon.layoutParams = layoutParams
                            
                            Timber.tag(TAG).d("成功加载车道图标: $resourceName, 尺寸: ${ICON_WIDTH}x${ICON_HEIGHT}")
                            logMemoryStatus("车道图标加载完成: $resourceName")
                            
                            // 调试当前尺寸
                            viewBinding.ivIcon.post {
                                Timber.tag(TAG).d("ImageView实际尺寸: ${viewBinding.ivIcon.width}x${viewBinding.ivIcon.height}")
                            }
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
            } else {
                Timber.tag(TAG).w("未找到raw资源: $resourceName")
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
                    // 从 raw 加载默认图标 ic_land_89
                    val defaultResourceId = mResources.getIdentifier("ic_land_89", "raw", mPackageName)
                    if (defaultResourceId != 0) {
                        val drawable = VectorDrawableCompat.create(mResources, defaultResourceId, null)
                        if (drawable != null) {
                            drawable.setBounds(0, 0, ICON_WIDTH, ICON_HEIGHT)
                            viewBinding.ivIcon.setImageDrawable(drawable)
                            
                            // 确保 ImageView 尺寸正确
                            val layoutParams = viewBinding.ivIcon.layoutParams
                            layoutParams.width = ICON_WIDTH
                            layoutParams.height = ICON_HEIGHT
                            viewBinding.ivIcon.layoutParams = layoutParams
                            
                            Timber.tag(TAG).d("使用默认 SVG 图标 - 位置: $position")
                        } else {
                            viewBinding.ivIcon.setImageDrawable(null)
                            Timber.tag(TAG).w("默认 SVG 图标加载失败 - 位置: $position")
                        }
                    } else {
                        viewBinding.ivIcon.setImageDrawable(null)
                        Timber.tag(TAG).w("未找到默认raw资源: ic_land_89")
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
        // 如果有缓存清理逻辑，可以在这里添加
    }
    
    /**
     * 获取缓存统计信息
     */
    fun getCacheStatistics(): String {
        return "当前使用 raw 资源加载，无缓存"
    }
}
