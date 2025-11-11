package com.fkdeepal.tools.ext.ui

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fkdeepal.tools.ext.AmapFloatManager
import com.fkdeepal.tools.ext.BuildConfig
import com.fkdeepal.tools.ext.R
import com.fkdeepal.tools.ext.base.BaseActivity
import com.fkdeepal.tools.ext.databinding.ActivityHudBinding
import com.fkdeepal.tools.ext.event.hud.HudCloseEvent
import com.fkdeepal.tools.ext.exts.toast
import com.fkdeepal.tools.ext.manager.UserDataManager
import com.fkdeepal.tools.ext.utils.PreferenceUtils
import com.jeremyliao.liveeventbus.LiveEventBus

class HudDisplayActivity : AppCompatActivity() {
    
    private val handler = Handler(Looper.getMainLooper())
    private var isAutoStartMode = false

    companion object{
        fun startActivity(context: Context,launchDisplayId:Int){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O   ) {
                val options = ActivityOptions.makeBasic()
                options.setLaunchDisplayId(launchDisplayId)
                val intent = Intent(context, HudDisplayActivity::class.java)
                if (context is Activity){

                }else{
                    intent. addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent, options.toBundle());
            }
        }

        fun startActivity(context: Context){
            val launchDisplayId =   UserDataManager.getHudDisplayId()
            if (launchDisplayId == null){
                context.toast("请先到设置页面中设置 投屏屏幕ID ")
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O   &&   Settings.canDrawOverlays(context)) {
                val options = ActivityOptions.makeBasic()
                options.setLaunchDisplayId(launchDisplayId)
                val intent = Intent(context, HudDisplayActivity::class.java)
                if (context is Activity){

                }else{
                    intent. addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                intent.putExtra("type",1)
                context.startActivity(intent, options.toBundle());
            }

        }

        // 新增：支持自启动模式的启动方法
        fun startActivity(context: Context, isAutoStart: Boolean) {
            val launchDisplayId = UserDataManager.getHudDisplayId()
            if (launchDisplayId == null) {
                context.toast("请先到设置页面中设置投屏屏幕ID")
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Settings.canDrawOverlays(context)) {
                val options = ActivityOptions.makeBasic()
                options.setLaunchDisplayId(launchDisplayId)
                val intent = Intent(context, HudDisplayActivity::class.java)
                if (context is Activity) {
                    // 保持原有逻辑
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                intent.putExtra("type", 1)
                intent.putExtra("is_auto_start", isAutoStart)
                context.startActivity(intent, options.toBundle())
            }
        }

        // 关闭HUD的方法 - 修复：使用正确的方法名 hideHudFloat
        fun closeHud(context: Context) {
            AmapFloatManager.hideHudFloat()
            context.toast("HUD已关闭")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hud)
        Log.d("HudDisplayActivity", "create")

        // 检查是否为自启动模式
        isAutoStartMode = intent.getBooleanExtra("is_auto_start", false) || 
                          PreferenceUtils.isAutoStartMode(this)

        val type = intent.getIntExtra("type", 1)
        when (type) {
            2 -> AmapFloatManager.showFloat(this, null, -1f)
            3 -> AmapFloatManager.showFloat(this, -1f, -1f)
            else -> AmapFloatManager.showFloat(this)
        }

        // 根据模式决定后续操作
        if (isAutoStartMode) {
            // 自启动模式：显示HUD后返回桌面
            handler.postDelayed({
                returnToHomeScreen()
            }, 1500) // 1.5秒后返回桌面，确保HUD完全启动
        } else {
            // 手动模式：2秒后关闭Activity（保持原有逻辑）
            handler.postDelayed({ 
                finish() 
            }, 2000)
        }
    }

    /**
     * 返回桌面（车机系统专用）
     */
    private fun returnToHomeScreen() {
        try {
            // 方式1：使用HOME Intent返回桌面
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            
            startActivity(homeIntent)
            
        } catch (e: Exception) {
            Log.e("HudDisplayActivity", "返回桌面失败: ${e.message}")
            // 如果返回桌面失败，至少关闭当前Activity
            finish()
        } finally {
            // 清除自启动模式标记
            PreferenceUtils.setAutoStartMode(this, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        
        // 确保清除自启动模式标记
        if (isAutoStartMode) {
            PreferenceUtils.setAutoStartMode(this, false)
        }
        
        Log.d("HudDisplayActivity", "destroy - isAutoStartMode: $isAutoStartMode")
    }
}
