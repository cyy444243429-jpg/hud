package com.fkdeepal.tools.ext.app

import android.app.Application
import com.fkdeepal.tools.ext.timber.FileLoggingTree
import com.fkdeepal.tools.ext.utils.AppUtils
import com.fkdeepal.tools.ext.utils.FileUtils
import timber.log.Timber

class AppApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        AppUtils.setContext(applicationContext)
        if (AppUtils.isDebug){
            Timber.plant(FileLoggingTree(FileUtils.getLogCacheFile(AppUtils.appContext)))
        }

    }
}