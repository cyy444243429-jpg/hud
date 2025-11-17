package com.fkdeepal.tools.ext.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.fkdeepal.tools.ext.timber.FileLoggingTree
import com.fkdeepal.tools.ext.utils.AppUtils
import com.fkdeepal.tools.ext.utils.FileUtils
import timber.log.Timber

class AppApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        // 启用矢量图资源支持
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        AppUtils.setContext(applicationContext)
        
        // 确保在调试模式下启用文件日志
        if (AppUtils.isDebug){
            Timber.plant(FileLoggingTree(FileUtils.getLogCacheFile(AppUtils.appContext)))
            Timber.d("文件日志系统已启用 - 新建文件日志记录功能已就绪")
        }
        
        // 同时添加DebugTree用于Logcat输出
        Timber.plant(Timber.DebugTree())
        Timber.d("应用程序初始化完成")
    }
}
