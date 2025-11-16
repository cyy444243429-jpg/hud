package com.fkdeepal.tools.ext.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.core.content.edit

class ColorPreferenceManager private constructor(context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: ColorPreferenceManager? = null
        
        fun getInstance(context: Context): ColorPreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ColorPreferenceManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences("land_colors", Context.MODE_PRIVATE)
    
    // 获取主颜色（灰色箭头）
    fun getLandPrimaryColor(): Int {
        return prefs.getInt("land_primary_color", Color.parseColor("#808080"))
    }
    
    // 获取次颜色（红色箭头）
    fun getLandSecondaryColor(): Int {
        return prefs.getInt("land_secondary_color", Color.parseColor("#FF0000"))
    }
    
    // 设置主颜色
    fun setLandPrimaryColor(color: Int) {
        prefs.edit {
            putInt("land_primary_color", color)
        }
        notifyColorChange()
    }
    
    // 设置次颜色
    fun setLandSecondaryColor(color: Int) {
        prefs.edit {
            putInt("land_secondary_color", color)
        }
        notifyColorChange()
    }
    
    // 重置为默认颜色
    fun resetToDefault() {
        prefs.edit {
            remove("land_primary_color")
            remove("land_secondary_color")
        }
        notifyColorChange()
    }
    
    private fun notifyColorChange() {
        // 这里可以发送事件通知界面更新，如果需要实时更新的话
        // LiveEventBus.get("land_colors_changed").post(true)
    }
}
