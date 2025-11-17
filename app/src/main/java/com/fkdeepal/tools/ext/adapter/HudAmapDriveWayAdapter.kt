// app/src/main/java/com/fkdeepal/tools/ext/adapter/HudAmapDriveWayAdapter.kt
package com.fkdeepal.tools.ext.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
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
            // 使用 SVG 加载器加载 SVG 文件
            val svgFileName = "$resourceName.xml"
            Timber.tag(TAG).d("SVG 文件名称: $svgFileName")
            
            val drawable = SvgLoader.loadSvgFromAssets(mContext, svgFileName)
            if (drawable != null) {
                // 在主线程安全设置图片
                viewBinding.root.post {
                    runCatching {
                        viewBinding.ivIcon.setImageDrawable(drawable)
                        Timber.tag(TAG).d("成功加载 SVG: $resourceName")
                        logMemoryStatus("SVG 加载完成: $resourceName")
                    }.onFailure { e ->
                        Timber.tag(TAG).e(e, "设置 SVG 图片时发生错误: $resourceName")
                    }
                }
            } else {
                Timber.tag(TAG).w("SVG 加载为null: $resourceName")
                viewBinding.root.post {
                    viewBinding.ivIcon.setImageDrawable(null)
                    fail.invoke()
                }
            }
        }.onFailure { exception ->
            Timber.tag(TAG).e(exception, "加载 SVG 失败: $resourceName")
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
                    val drawable = SvgLoader.loadSvgFromAssets(mContext, "ic_land_89.xml")
                    if (drawable != null) {
                        viewBinding.ivIcon.setImageDrawable(drawable)
                        Timber.tag(TAG).d("使用默认 SVG 图标 - 位置: $position")
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
}
