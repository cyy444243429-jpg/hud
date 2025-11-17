package com.fkdeepal.tools.ext.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.fkdeepal.tools.ext.timber.FileLoggingTree
import com.fkdeepal.tools.ext.utils.AppUtils
import com.fkdeepal.tools.ext.utils.FileUtils
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppApplication : Application(){
    
    override fun onCreate() {
        super.onCreate()
        
        // 设置全局未捕获异常处理器
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleUncaughtException(thread, throwable)
        }
        
        // 启用矢量图资源支持
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        AppUtils.setContext(applicationContext)
        if (AppUtils.isDebug){
            Timber.plant(FileLoggingTree(FileUtils.getLogCacheFile(AppUtils.appContext)))
            Timber.d("文件日志系统已启用 - 全局异常捕获已设置")
        }
        
        // 添加DebugTree用于Logcat输出
        Timber.plant(Timber.DebugTree())
    }
    
    private fun handleUncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            val stackTrace = sw.toString()
            
            // 记录到Timber
            Timber.e(throwable, "=== 应用程序崩溃 ===")
            Timber.e("崩溃线程: ${thread.name} (ID: ${thread.id})")
            Timber.e("异常类型: ${throwable.javaClass.name}")
            Timber.e("异常信息: ${throwable.message}")
            Timber.e("堆栈跟踪:\n$stackTrace")
            
            // 获取内存信息
            val runtime = Runtime.getRuntime()
            val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
            val maxMemory = runtime.maxMemory() / (1024 * 1024)
            Timber.e("内存状态 - 已用: ${usedMemory}MB, 最大: ${maxMemory}MB, 可用: ${maxMemory - usedMemory}MB")
            
            // 保存到独立崩溃日志文件
            saveCrashLog(thread, throwable, stackTrace, usedMemory, maxMemory)
            
        } catch (e: Exception) {
            // 防止异常处理过程中再次崩溃
            Timber.e(e, "异常处理过程中发生错误")
        } finally {
            // 等待日志写入完成
            Thread.sleep(1000)
            // 退出应用
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        }
    }
    
    private fun saveCrashLog(thread: Thread, throwable: Throwable, stackTrace: String, usedMemory: Long, maxMemory: Long) {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val crashFile = FileUtils.getLogCacheFile(AppUtils.appContext, "crash_${timeStamp}.txt")
            
            val crashInfo = """
            |=== 应用程序崩溃报告 ===
            |崩溃时间: ${Date()}
            |应用版本: ${AppUtils.getVersionName()} (${AppUtils.getVersionCode()})
            |设备信息: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
            |Android版本: ${android.os.Build.VERSION.RELEASE} (SDK: ${android.os.Build.VERSION.SDK_INT})
            |
            |=== 异常信息 ===
            |崩溃线程: ${thread.name} (ID: ${thread.id})
            |异常类型: ${throwable.javaClass.name}
            |异常信息: ${throwable.message}
            |
            |=== 内存状态 ===
            |已用内存: ${usedMemory}MB
            |最大内存: ${maxMemory}MB
            |可用内存: ${maxMemory - usedMemory}MB
            |
            |=== 堆栈跟踪 ===
            |$stackTrace
            |
            |=== 线程状态 ===
            |${getAllThreadsInfo()}
            """.trimMargin()
            
            crashFile.writeText(crashInfo)
            Timber.e("崩溃日志已保存到: ${crashFile.absolutePath}")
            
        } catch (e: Exception) {
            Timber.e(e, "保存崩溃日志失败")
        }
    }
    
    private fun getAllThreadsInfo(): String {
        return try {
            val threadSet = Thread.getAllStackTraces()
            val sb = StringBuilder()
            threadSet.forEach { (thread, stackTrace) ->
                sb.append("线程: ${thread.name} (ID: ${thread.id}, 状态: ${thread.state})\n")
                if (stackTrace.isNotEmpty()) {
                    sb.append("  堆栈:\n")
                    stackTrace.take(5).forEach { element ->
                        sb.append("    $element\n")
                    }
                }
                sb.append("\n")
            }
            sb.toString()
        } catch (e: Exception) {
            "获取线程信息失败: ${e.message}"
        }
    }
}
