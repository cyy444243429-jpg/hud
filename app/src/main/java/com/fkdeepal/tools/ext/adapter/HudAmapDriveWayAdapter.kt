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
        Timber.tag(TAG).d("创建视图绑定")
        return ItemHubDriveWayBinding.inflate(layoutInflater, parent, false)
    }

    fun setImageDrawable(viewBinding: ItemHubDriveWayBinding, resourceName: String,
                         fail: () -> Unit) {
        
        // 添加内存监控
        logMemoryStatus("开始加载图标: $resourceName")

        runCatching {
            // 使用更安全的矢量图加载方式
            val resourceId = mResources.getIdentifier(resourceName, "drawable", mPackageName)
            Timber.tag(TAG).d("资源ID查询: $resourceName -> $resourceId")
            
            if (resourceId != 0) {
                // 使用 AppCompatResources 加载矢量图
                val drawable = AppCompatResources.getDrawable(mContext, resourceId)
                if (drawable != null) {
                    // 在主线程安全设置图片
                    viewBinding.root.post {
                        runCatching {
                            viewBinding.ivIcon.setImageDrawable(drawable)
                            Timber.tag(TAG).d("成功加载矢量图: $resourceName")
                            logMemoryStatus("图标加载完成: $resourceName")
                        }.onFailure { e ->
                            Timber.tag(TAG).e(e, "设置图片时发生错误: $resourceName")
                        }
                    }
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
        // 添加内存监控
        logMemoryStatus("开始设置车道数据 - 位置: $position")
        
        runCatching {
            val icon = item.drive_way_lane_Back_icon
            Timber.tag(TAG).d("设置车道数据 - 位置: $position, 图标: $icon")
            
            viewBinding.tvValue.visibility = View.GONE
            
            if (icon.isNullOrBlank()) {
                // 使用安全的默认资源加载
                runCatching {
                    val drawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_land_89)
                    viewBinding.ivIcon.setImageDrawable(drawable)
                    Timber.tag(TAG).d("使用默认图标 - 位置: $position")
                    logMemoryStatus("默认图标设置完成 - 位置: $position")
                }.onFailure {
                    Timber.tag(TAG).e(it, "加载默认图标失败 - 位置: $position")
                    viewBinding.ivIcon.setImageDrawable(null)
                }
            } else {
                // 恢复完整的 ic_land_xx 图标加载
                val resourceName = "ic_land_${item.drive_way_lane_Back_icon}"
                Timber.tag(TAG).d("加载ic_land_xx图标: $resourceName - 位置: $position")
                setImageDrawable(viewBinding, resourceName) {
                    Timber.tag(TAG).d("图标加载失败，显示文本: $icon")
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
