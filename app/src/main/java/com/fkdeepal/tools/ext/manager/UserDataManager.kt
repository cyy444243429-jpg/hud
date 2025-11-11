package com.fkdeepal.tools.ext.manager

import com.fkdeepal.tools.ext.BuildConfig
import com.fkdeepal.tools.ext.utils.AppUtils
import com.fkdeepal.tools.ext.utils.PreferenceUtils

object UserDataManager {

    fun getHudDisplayId(): Int?{
        val id =  PreferenceUtils.getString(AppUtils.appContext,"key_setting_hud_display_id","").toIntOrNull()
       return  id
    }

}