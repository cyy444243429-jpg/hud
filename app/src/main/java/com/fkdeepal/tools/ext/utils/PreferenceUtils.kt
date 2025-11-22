package com.fkdeepal.tools.ext.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import timber.log.Timber

object PreferenceUtils {

    @JvmStatic
    fun getString(context: Context, key: String, defaultValue: String): String {
        return getString(context, null, key, defaultValue)
    }

    @JvmStatic
    fun putString(context: Context, key: String, value: String) {
        putString(context, null, key, value)
    }

    @JvmStatic
    fun getBoolean(context: Context, key: String,
                   defaultValue: Boolean): Boolean {
        return getBoolean(context, null, key, defaultValue)
    }

    @JvmStatic
    fun hasKey(context: Context, key: String): Boolean {
        return hasKey(context, null, key)
    }

    @JvmStatic
    fun putBoolean(context: Context, key: String, value: Boolean) {
        putBoolean(context, null, key, value)
    }

    @JvmStatic
    fun putInt(context: Context, key: String, value: Int) {
        putInt(context, null, key, value)
    }

    @JvmStatic
    fun getInt(context: Context, key: String, defaultValue: Int): Int {
        return getInt(context, null, key, defaultValue)
    }

    @JvmStatic
    fun putFloat(context: Context, key: String, value: Float) {
        putFloat(context, null, key, value)
    }

    @JvmStatic
    fun getFloat(context: Context, key: String, defaultValue: Float): Float {
        return getFloat(context, null, key, defaultValue)
    }

    @JvmStatic
    fun putLong(context: Context, key: String, value: Long) {
        putLong(context, null, key, value)
    }

    @JvmStatic
    fun getLong(context: Context, key: String, defaultValue: Long): Long {
        return getLong(context, null, key, defaultValue)
    }

    @JvmStatic
    fun getString(context: Context, preferencesFileName: String?, key: String, defaultValue: String): String {
        val sp = getSharedPreferences(context,preferencesFileName)
        val value = sp.getString(key, defaultValue)
        if (value == null) return ""
        return value
    }

    @JvmStatic
    fun putString(context: Context, preferencesFileName: String?, key: String, value: String) {
        val sp = getSharedPreferences(context,preferencesFileName)
        sp.edit().putString(key, value).commit()
    }


    @JvmStatic
    fun getBoolean(context: Context, preferencesFileName: String?, key: String,
                   defaultValue: Boolean): Boolean {
        val sp = getSharedPreferences(context,preferencesFileName)
        return sp.getBoolean(key, defaultValue)
    }

    @JvmStatic
    fun hasKey(context: Context, preferencesFileName: String?, key: String): Boolean {
        return getSharedPreferences(context,preferencesFileName).contains(key)
    }

    @JvmStatic
    fun putBoolean(context: Context, preferencesFileName: String?, key: String, value: Boolean) {
        val sp = getSharedPreferences(context,preferencesFileName)
        sp.edit().putBoolean(key, value).apply()
    }

    @JvmStatic
    fun putInt(context: Context, preferencesFileName: String?, key: String, value: Int) {
        val sp = getSharedPreferences(context,preferencesFileName)
        sp.edit().putInt(key, value).apply()
    }

    @JvmStatic
    fun getInt(context: Context, preferencesFileName: String?, key: String, defaultValue: Int): Int {
        val sp = getSharedPreferences(context,preferencesFileName)
        return sp.getInt(key, defaultValue)
    }

    @JvmStatic
    fun putFloat(context: Context, preferencesFileName: String?, key: String, value: Float) {
        val sp =   if (preferencesFileName.isNullOrBlank()){
            PreferenceManager.getDefaultSharedPreferences(context)
        }else{
            getSharedPreferences(context,preferencesFileName)
        }

        sp.edit().putFloat(key, value).apply()
    }

    @JvmStatic
    fun getFloat(context: Context, preferencesFileName: String?, key: String, defaultValue: Float): Float {
        val sp = getSharedPreferences(context,preferencesFileName)
        return sp.getFloat(key, defaultValue)
    }

    @JvmStatic
    fun putLong(context: Context, preferencesFileName: String?, key: String, value: Long) {
        val sp = getSharedPreferences(context,preferencesFileName)
        sp.edit().putLong(key, value).apply()
    }

    @JvmStatic
    fun getLong(context: Context, preferencesFileName: String?, key: String, defaultValue: Long): Long {
        val sp = getSharedPreferences(context,preferencesFileName)
        return sp.getLong(key, defaultValue)
    }

    @JvmStatic
    fun removeKey(context: Context, preferencesFileName: String?, key: String) {
        val sp = getSharedPreferences(context,preferencesFileName)
        val editor = sp.edit()
        editor.remove(key)
        editor.apply()
    }
    fun getSharedPreferences(context: Context, preferencesFileName: String?):SharedPreferences{
        if (preferencesFileName.isNullOrBlank()){
            return PreferenceManager.getDefaultSharedPreferences(context)
        }
        return  context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE)
    }
    /**
     * 清空 SharedPreferences
     * @param context Context
     * @param preferencesFileName  SharedPreferences 名
     * @param isApply 使用apply()或者commit()方式
     *
     */
    @JvmStatic
    fun clearPreference(context: Context, preferencesFileName: String?, isApply: Boolean = true) {
        val sp = getSharedPreferences(context,preferencesFileName)
        val editor = sp.edit()
        editor.clear()
        if (isApply) {
            editor.apply()
        } else {
            editor.commit()
        }

    }

    @JvmStatic
    fun clearPreference(context: Context, sp: SharedPreferences, isApply: Boolean = true) {
        val editor = sp.edit()
        editor.clear()
        if (isApply) {
            editor.apply()
        } else {
            editor.commit()
        }
    }

    // 自启动相关常量
    private const val KEY_AUTO_START = "auto_start"
    private const val KEY_IS_AUTO_START_MODE = "is_auto_start_mode"

    // 自启动开关状态
    @JvmStatic
    fun setAutoStartEnabled(context: Context, enabled: Boolean) {
        putBoolean(context, KEY_AUTO_START, enabled)
    }

    @JvmStatic
    fun isAutoStartEnabled(context: Context): Boolean {
        return getBoolean(context, KEY_AUTO_START, false)
    }

    // 标记当前是否为自启动模式
    @JvmStatic
    fun setAutoStartMode(context: Context, isAutoMode: Boolean) {
        putBoolean(context, KEY_IS_AUTO_START_MODE, isAutoMode)
    }

    @JvmStatic
    fun isAutoStartMode(context: Context): Boolean {
        return getBoolean(context, KEY_IS_AUTO_START_MODE, false)
    }

    // ========== 新增：车道图标大小设置 ==========
    
    private const val KEY_LAND_ICON_SIZE = "key_land_icon_size"
    
    /**
     * 获取车道图标大小
     */
    @JvmStatic
    fun getLandIconSize(context: Context): Int {
        return getInt(context, KEY_LAND_ICON_SIZE, 55) // 默认55px
    }
    
    /**
     * 设置车道图标大小
     */
    @JvmStatic
    fun setLandIconSize(context: Context, size: Int) {
        putInt(context, KEY_LAND_ICON_SIZE, size)
        Timber.d("设置车道图标大小: ${size}px")
    }
    
    /**
     * 获取车道图标宽度（根据高度自动计算比例）
     */
    @JvmStatic
    fun getLandIconWidth(context: Context): Int {
        val height = getLandIconSize(context)
        // 根据40:55的比例计算宽度
        return (height * 40 / 55)
    }

    // ========== 新增：车道图标间距设置 ==========
    
    private const val KEY_LAND_ICON_SPACING = "key_land_icon_spacing"
    
    /**
     * 获取车道图标间距
     */
    @JvmStatic
    fun getLandIconSpacing(context: Context): Int {
        return getInt(context, KEY_LAND_ICON_SPACING, 0) // 默认0px
    }
    
    /**
     * 设置车道图标间距
     */
    @JvmStatic
    fun setLandIconSpacing(context: Context, spacing: Int) {
        putInt(context, KEY_LAND_ICON_SPACING, spacing)
        Timber.d("设置车道图标间距: ${spacing}px")
    }
}
