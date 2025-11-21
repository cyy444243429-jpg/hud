package com.fkdeepal.tools.ext.utils

import android.graphics.Color
import com.fkdeepal.tools.ext.utils.AppUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import timber.log.Timber

object ColorPreferenceManager {
    
    companion object {
        private const val TAG = "ColorPreferenceManager"
    }
    
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
        notifyPrimaryColorChanged(color)
        updateAllVisibleIcons()
    }
    
    // 保存次颜色
    fun setSecondaryColor(color: Int) {
        Timber.tag(TAG).i("设置次颜色: ${String.format("#%06X", 0xFFFFFF and color)}")
        PreferenceUtils.putInt(AppUtils.appContext, "secondary_color_land", color)
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
        LiveEventBus.get("land_color_update", Boolean::class.java).post(true)
    }
    
    // 重置颜色
    fun resetColors() {
        Timber.tag(TAG).i("重置颜色为默认值")
        setPrimaryColor(Color.WHITE)
        setSecondaryColor(Color.RED)
    }
}
