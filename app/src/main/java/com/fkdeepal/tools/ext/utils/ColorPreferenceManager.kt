package com.fkdeepal.tools.ext.utils

import android.graphics.Color
import com.fkdeepal.tools.ext.utils.AppUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import timber.log.Timber

object ColorPreferenceManager {
    
    private const val TAG = "ColorPreferenceManager"
    
    // 颜色变化监听器
    interface OnColorChangeListener {
        fun onPrimaryColorChanged(color: Int)
        fun onSecondaryColorChanged(color: Int)
    }
    
    private val colorListeners = mutableListOf<OnColorChangeListener>()
    
    // 保存主颜色
    fun setPrimaryColor(color: Int) {
        Timber.tag(TAG).i("设置主颜色: ${String.format("#%06X", 0xFFFFFF and color)}")
        PreferenceUtils.putInt(AppUtils.appContext, "primary_color_land", color)
        
        // 同步到SVG加载器
        val colorHex = String.format("#%06X", 0xFFFFFF and color)
        val secondaryColorHex = String.format("#%06X", 0xFFFFFF and getSecondaryColor())
        com.fkdeepal.tools.ext.utils.SvgLoader.updateColors(colorHex, secondaryColorHex)
        
        notifyPrimaryColorChanged(color)
        updateAllVisibleIcons()
    }
    
    // 保存次颜色
    fun setSecondaryColor(color: Int) {
        Timber.tag(TAG).i("设置次颜色: ${String.format("#%06X", 0xFFFFFF and color)}")
        PreferenceUtils.putInt(AppUtils.appContext, "secondary_color_land", color)
        
        // 同步到SVG加载器
        val primaryColorHex = String.format("#%06X", 0xFFFFFF and getPrimaryColor())
        val colorHex = String.format("#%06X", 0xFFFFFF and color)
        com.fkdeepal.tools.ext.utils.SvgLoader.updateColors(primaryColorHex, colorHex)
        
        notifySecondaryColorChanged(color)
        updateAllVisibleIcons()
    }
    
    // 获取主颜色
    fun getPrimaryColor(): Int {
        val color = PreferenceUtils.getInt(AppUtils.appContext, "primary_color_land", Color.WHITE)
        Timber.tag(TAG).d("获取主颜色: ${String.format("#%06X", 0xFFFFFF and color)}")
        return color
    }
    
    // 获取次颜色
    fun getSecondaryColor(): Int {
        val color = PreferenceUtils.getInt(AppUtils.appContext, "secondary_color_land", Color.RED)
        Timber.tag(TAG).d("获取次颜色: ${String.format("#%06X", 0xFFFFFF and color)}")
        return color
    }
    
    // 注册监听器
    fun addColorChangeListener(listener: OnColorChangeListener) {
        if (!colorListeners.contains(listener)) {
            colorListeners.add(listener)
            Timber.tag(TAG).d("注册颜色变化监听器，当前监听器数量: ${colorListeners.size}")
        }
    }
    
    // 移除监听器
    fun removeColorChangeListener(listener: OnColorChangeListener) {
        colorListeners.remove(listener)
        Timber.tag(TAG).d("移除颜色变化监听器，当前监听器数量: ${colorListeners.size}")
    }
    
    private fun notifyPrimaryColorChanged(color: Int) {
        Timber.tag(TAG).d("通知主颜色变更给 ${colorListeners.size} 个监听器")
        colorListeners.forEach { it.onPrimaryColorChanged(color) }
    }
    
    private fun notifySecondaryColorChanged(color: Int) {
        Timber.tag(TAG).d("通知次颜色变更给 ${colorListeners.size} 个监听器")
        colorListeners.forEach { it.onSecondaryColorChanged(color) }
    }
    
    private fun updateAllVisibleIcons() {
        Timber.tag(TAG).d("发送颜色更新事件，更新所有可见图标")
        
        // 清理SVG缓存，强制重新加载和着色
        com.fkdeepal.tools.ext.utils.SvgLoader.clearCache()
        
        LiveEventBus.get("land_color_update", Boolean::class.java).post(true)
    }
    
    // 重置颜色
    fun resetColors() {
        Timber.tag(TAG).i("重置颜色为默认值")
        setPrimaryColor(Color.WHITE)
        setSecondaryColor(Color.RED)
    }
    
    // ========== 新增：高亮显示功能 ==========
    
    // 高亮显示开关
    fun setHighlightEnabled(enabled: Boolean) {
        Timber.tag(TAG).i("设置高亮显示开关: $enabled")
        PreferenceUtils.putBoolean(AppUtils.appContext, "highlight_enabled", enabled)
        // 如果关闭高亮，立即清除高亮状态
        if (!enabled) {
            setHighlightActive(false)
        }
    }
    
    fun isHighlightEnabled(): Boolean {
        return PreferenceUtils.getBoolean(AppUtils.appContext, "highlight_enabled", true)
    }
    
    // 高亮状态管理
    private var isHighlightActive = false
    private var lastHighlightTime = 0L
    
    fun setHighlightActive(active: Boolean) {
        if (isHighlightActive != active) {
            isHighlightActive = active
            lastHighlightTime = if (active) System.currentTimeMillis() else 0
            Timber.tag(TAG).d("高亮状态变更: $active")
            
            // 同步到SVG加载器
            com.fkdeepal.tools.ext.utils.SvgLoader.setHighlightActive(active)
            
            // 状态变化时强制刷新图标
            updateAllVisibleIcons()
        }
    }
    
    fun isHighlightActive(): Boolean {
        return isHighlightActive && isHighlightEnabled()
    }
    
    // 获取高亮颜色（跑马灯效果）
    fun getHighlightColor(): Int {
        return calculateRunningLightColor()
    }
    
    private fun calculateRunningLightColor(): Int {
        // 基于时间计算渐变颜色（2秒循环）
        val time = System.currentTimeMillis() % 2000
        val progress = (time / 2000.0f).coerceIn(0f, 1f)
        
        return when {
            progress < 0.33f -> Color.rgb(255, (255 * progress * 3).toInt(), 0) // 红→黄
            progress < 0.66f -> Color.rgb((255 * (1 - (progress - 0.33f) * 3)).toInt(), 255, 0) // 黄→绿
            else -> Color.rgb(0, 255, (255 * (progress - 0.66f) * 3).toInt()) // 绿→青
        }
    }
    
    // 清除高亮状态（当导航结束或距离超过100米时调用）
    fun clearHighlight() {
        if (isHighlightActive) {
            Timber.tag(TAG).d("清除高亮状态")
            setHighlightActive(false)
        }
    }
    
    // 获取高亮状态持续时间（用于调试）
    fun getHighlightDuration(): Long {
        return if (isHighlightActive) System.currentTimeMillis() - lastHighlightTime else 0
    }
    
    // 初始化时同步颜色
    init {
        // 应用启动时同步初始颜色
        val primaryColorHex = String.format("#%06X", 0xFFFFFF and getPrimaryColor())
        val secondaryColorHex = String.format("#%06X", 0xFFFFFF and getSecondaryColor())
        com.fkdeepal.tools.ext.utils.SvgLoader.updateColors(primaryColorHex, secondaryColorHex)
    }
}
