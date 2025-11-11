package com.fkdeepal.tools.ext.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

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
}