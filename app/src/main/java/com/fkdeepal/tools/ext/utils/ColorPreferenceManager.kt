package com.fkdeepal.tools.ext.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.core.content.edit
import timber.log.Timber

class ColorPreferenceManager private constructor(context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: ColorPreferenceManager? = null
        
        fun getInstance(context: Context): ColorPreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ColorPreferenceManager(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        private const val TAG = "ColorPreferenceManager"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences("land_colors", Context.MODE_PRIVATE)
    
    // 获取主颜色（灰色箭头）
    fun getLandPrimaryColor(): Int {
        val color = prefs.getInt("land_primary_color", Color.parseColor("#808080"))
        Timber.tag(TAG).d("获取主颜色: ${String.format("#%06X", 0xFFFFFF and color)}")
        return color
    }
    
    // 获取次颜色（红色箭头）
    fun getLandSecondaryColor(): Int {
        val color = prefs.getInt("land_secondary_color", Color.parseColor("#FF0000"))
        Timber.tag(TAG).d("获取次颜色: ${String.format("#%06X", 0xFFFFFF and color)}")
        return color
    }
    
    // 设置主颜色
    fun setLandPrimaryColor(color: Int) {
        Timber.tag(TAG).i("设置主颜色: ${String.format("#%06X", 0xFFFFFF and color)}")
        prefs.edit {
            putInt("land_primary_color", color)
        }
        notifyColorChange()
    }
    
    // 设置次颜色
    fun setLandSecondaryColor(color: Int) {
        Timber.tag(TAG).i("设置次颜色: ${String.format("#%06X", 0xFFFFFF and color)}")
        prefs.edit {
            putInt("land_secondary_color", color)
        }
        notifyColorChange()
    }
    
    // 重置为默认颜色
    fun resetToDefault() {
        Timber.tag(TAG).i("重置颜色为默认值")
        prefs.edit {
            remove("land_primary_color")
            remove("land_secondary_color")
        }
        notifyColorChange()
    }
    
    private fun notifyColorChange() {
        Timber.tag(TAG).d("颜色变更通知发送")
        // 这里可以发送事件通知界面更新，如果需要实时更新的话
        // LiveEventBus.get("land_colors_changed").post(true)
    }
}
