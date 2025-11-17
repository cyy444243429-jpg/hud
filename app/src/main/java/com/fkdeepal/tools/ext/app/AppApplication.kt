// app/src/main/java/com/fkdeepal/tools/ext/app/AppApplication.kt
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
    
    companion object {
        private const val TAG = "AppApplication"
        private var lastOperation: String = "应用启动"
        private val operationHistory = mutableListOf<String>()
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // 记录关键操作
        logOperation("AppApplication.onCreate()")
        
        // 设置全局未捕获异常处理器
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logOperation("未捕获异常处理开始 - 线程: ${thread.name}")
            handleUncaughtException(thread, throwable)
        }
        
        // 启用矢量图资源支持
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        AppUtils.setContext(applicationContext)
        
        // 初始化日志
        if (AppUtils.isDebug){
            Timber.plant(FileLoggingTree(FileUtils.getLogCacheFile(AppUtils.appContext)))
            Timber.d("文件日志系统已启用 - 全局异常捕获已设置")
        }
        Timber.plant(Timber.DebugTree())
        
        logOperation("AppApplication.onCreate() 完成")
    }
    
    // 记录关键操作的方法
    fun logOperation(operation: String) {
        lastOperation = operation
        operationHistory.add("${SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())}: $operation")
        
        // 保持操作历史在合理大小
        if (operationHistory.size > 50) {
            operationHistory.removeAt(0)
        }
        
        Timber.tag(TAG).d("操作记录: $operation")
    }
    
    // 获取最近的操作历史
    fun getRecentOperations(): String {
        return operationHistory.takeLast(30).joinToString("\n")
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
            Timber.e("最后操作: $lastOperation")
            
            // 记录操作历史
            Timber.e("最近操作历史:\n${getRecentOperations()}")
            
            // 记录所有活跃线程
            logAllThreads()
            
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
            Thread.sleep(2000)
            // 退出应用
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        }
    }
    
    private fun logAllThreads() {
        try {
            val threadSet = Thread.getAllStackTraces()
            Timber.e("=== 所有线程状态 ===")
            threadSet.forEach { (thread, stackTrace) ->
                Timber.e("线程: ${thread.name} (ID: ${thread.id}, 状态: ${thread.state}, 优先级: ${thread.priority})")
                if (stackTrace.isNotEmpty()) {
                    Timber.e("  堆栈:")
                    stackTrace.take(5).forEach { element ->
                        Timber.e("    $element")
                    }
                }
                Timber.e("---")
            }
        } catch (e: Exception) {
            Timber.e("获取线程信息失败: ${e.message}")
        }
    }
    
    private fun saveCrashLog(thread: Thread, throwable: Throwable, stackTrace: String, usedMemory: Long, maxMemory: Long) {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            
            // 创建独立的崩溃日志文件
            val crashFileName = "crash_${timeStamp}.txt"
            val crashFile = java.io.File(AppUtils.appContext.filesDir, crashFileName)
            
            // 获取版本信息
            val packageInfo = AppUtils.appContext.packageManager.getPackageInfo(AppUtils.appContext.packageName, 0)
            val versionName = packageInfo.versionName
            val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toString()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toString()
            }
            
            val crashInfo = """
            |=== 应用程序崩溃报告 ===
            |崩溃时间: ${Date()}
            |应用版本: $versionName ($versionCode)
            |设备信息: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
            |Android版本: ${android.os.Build.VERSION.RELEASE} (SDK: ${android.os.Build.VERSION.SDK_INT})
            |
            |=== 崩溃信息 ===
            |崩溃线程: ${thread.name} (ID: ${thread.id})
            |异常类型: ${throwable.javaClass.name}
            |异常信息: ${throwable.message}
            |最后操作: $lastOperation
            |
            |=== 操作历史 (最近30个) ===
            |${getRecentOperations()}
            |
            |=== 内存状态 ===
            |已用内存: ${usedMemory}MB
            |最大内存: ${maxMemory}MB
            |可用内存: ${maxMemory - usedMemory}MB
            |
            |=== 堆栈跟踪 ===
            |$stackTrace
            """.trimMargin()
            
            crashFile.writeText(crashInfo)
            Timber.e("崩溃日志已保存到: ${crashFile.absolutePath}")
            
        } catch (e: Exception) {
            Timber.e(e, "保存崩溃日志失败")
        }
    }
}
