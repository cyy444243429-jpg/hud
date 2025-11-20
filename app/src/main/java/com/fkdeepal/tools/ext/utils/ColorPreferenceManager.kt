package com.fkdeepal.tools.ext.utils

import android.graphics.Color
import com.fkdeepal.tools.ext.utils.AppUtils
import com.jeremyliao.liveeventbus.LiveEventBus

object ColorPreferenceManager {
    
    // 颜色变化监听器
    interface OnColorChangeListener {
        fun onPrimaryColorChanged(color: Int)
        fun onSecondaryColorChanged(color: Int)
    }
    
    private val colorListeners = mutableListOf<OnColorChangeListener>()
    
    // 保存主颜色
    fun setPrimaryColor(color: Int) {
        PreferenceUtils.putInt(AppUtils.appContext, "primary_color_land", color)
        notifyPrimaryColorChanged(color)
        updateAllVisibleIcons()
    }
    
    // 保存次颜色
    fun setSecondaryColor(color: Int) {
        PreferenceUtils.putInt(AppUtils.appContext, "secondary_color_land", color)
        notifySecondaryColorChanged(color)
        updateAllVisibleIcons()
    }
    
    // 获取主颜色
    fun getPrimaryColor(): Int {
        return PreferenceUtils.getInt(AppUtils.appContext, "primary_color_land", Color.WHITE)
    }
    
    // 获取次颜色
    fun getSecondaryColor(): Int {
        return PreferenceUtils.getInt(AppUtils.appContext, "secondary_color_land", Color.RED)
    }
    
    // 注册监听器
    fun addColorChangeListener(listener: OnColorChangeListener) {
        if (!colorListeners.contains(listener)) {
            colorListeners.add(listener)
        }
    }
    
    // 移除监听器
    fun removeColorChangeListener(listener: OnColorChangeListener) {
        colorListeners.remove(listener)
    }
    
    private fun notifyPrimaryColorChanged(color: Int) {
        colorListeners.forEach { it.onPrimaryColorChanged(color) }
    }
    
    private fun notifySecondaryColorChanged(color: Int) {
        colorListeners.forEach { it.onSecondaryColorChanged(color) }
    }
    
    private fun updateAllVisibleIcons() {
        LiveEventBus.get("land_color_update", Boolean::class.java).post(true)
    }
    
    // 重置颜色
    fun resetColors() {
        setPrimaryColor(Color.WHITE)
        setSecondaryColor(Color.RED)
    }
}
