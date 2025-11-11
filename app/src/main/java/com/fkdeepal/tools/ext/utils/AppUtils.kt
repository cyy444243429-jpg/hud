package com.fkdeepal.tools.ext.utils

import android.content.Context

object AppUtils {
    @JvmStatic
    lateinit var appContext: Context

    fun setContext(context: Context){
        appContext = context
        isDebug = PreferenceUtils.getBoolean(context,"key_is_debug",false)
    }
    var isDebug: Boolean   = false

}