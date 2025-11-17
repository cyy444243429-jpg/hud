package com.fkdeepal.tools.ext.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fkdeepal.tools.ext.R
import com.fkdeepal.tools.ext.utils.AppUtils
import com.fkdeepal.tools.ext.utils.PreferenceUtils
import com.fkdeepal.tools.ext.ui.video.HudVideoActivity
import com.fkdeepal.tools.ext.ui.setting.SettingActivity
import com.fkdeepal.tools.ext.ui.test.TestActivity
import com.fkdeepal.tools.ext.ui.log.LogFileListActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var switchAutoStart: Switch
    private lateinit var btnNaviHud: Button
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()
        
        // 检查是否开启自启动并自动跳转（只在HUD服务没有运行时）
        checkAutoStart()
    }

    private fun initViews() {
        switchAutoStart = findViewById(R.id.switchAutoStart)
        btnNaviHud = findViewById(R.id.btnNaviHud)
        
        // 设置开关状态
        switchAutoStart.isChecked = PreferenceUtils.isAutoStartEnabled(this)
        
        // 显示开发模式按钮（包括Log按钮）
        if (AppUtils.isDebug) {
            findViewById<LinearLayout>(R.id.layoutDev)?.visibility = View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        // 自启动开关监听
        switchAutoStart.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils.setAutoStartEnabled(this, isChecked)
        }

        // 启动导航HUD按钮 - 手动模式
        btnNaviHud.setOnClickListener {
            startHudDisplayActivity(false) // false表示手动启动
        }

        // 其他按钮监听保持原有逻辑...
        findViewById<Button>(R.id.btnNaviHudClose).setOnClickListener {
            // 关闭导航HUD的逻辑
            HudDisplayActivity.closeHud(this)
        }

        findViewById<Button>(R.id.btnVideo).setOnClickListener {
            startActivity(Intent(this, HudVideoActivity::class.java))
        }

        findViewById<Button>(R.id.btnSetting).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        
        // 开发模式按钮
        findViewById<Button>(R.id.btnTest)?.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
        
        findViewById<Button>(R.id.btnLog)?.setOnClickListener {
            startActivity(Intent(this, LogFileListActivity::class.java))
        }
    }

    private fun checkAutoStart() {
        // 只有在自启动开启且HUD服务没有运行时才执行
        if (PreferenceUtils.isAutoStartEnabled(this) && !HudDisplayActivity.isHudServiceRunning()) {
            // 延迟一段时间再跳转，让界面先显示出来
            handler.postDelayed({
                startHudDisplayActivity(true) // true表示自启动模式
            }, 800)
        }
    }

    private fun startHudDisplayActivity(isAutoStart: Boolean) {
        if (Settings.canDrawOverlays(this)) {
            // 标记启动模式
            PreferenceUtils.setAutoStartMode(this, isAutoStart)
            
            // 使用新的启动方法，传递自启动模式
            HudDisplayActivity.startActivity(this, isAutoStart)
            
            if (isAutoStart) {
                // 如果是自启动模式，立即finish当前页面
                finish()
            }
        } else {
            Toast.makeText(this, "请先授予悬浮窗权限", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
