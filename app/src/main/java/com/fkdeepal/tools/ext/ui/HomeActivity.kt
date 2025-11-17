package com.fkdeepal.tools.ext.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fkdeepal.tools.ext.R
import com.fkdeepal.tools.ext.utils.PreferenceUtils
import com.fkdeepal.tools.ext.utils.ColorPreferenceManager
import com.fkdeepal.tools.ext.ui.video.HudVideoActivity
import com.fkdeepal.tools.ext.ui.setting.SettingActivity
import com.fkdeepal.tools.ext.ui.test.TestActivity
import com.fkdeepal.tools.ext.ui.log.LogFileListActivity
import timber.log.Timber

class HomeActivity : AppCompatActivity() {

    private lateinit var switchAutoStart: Switch
    private lateinit var btnNaviHud: Button
    private val handler = Handler(Looper.getMainLooper())
    
    companion object {
        private const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(TAG).d("HomeActivity创建")
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()
        
        // 检查是否开启自启动并自动跳转（只在HUD服务没有运行时）
        checkAutoStart()
    }
    
    override fun onResume() {
        super.onResume()
        Timber.tag(TAG).d("HomeActivity恢复")
    }
    
    override fun onPause() {
        super.onPause()
        Timber.tag(TAG).d("HomeActivity暂停")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(TAG).d("HomeActivity销毁")
        handler.removeCallbacksAndMessages(null)
    }

    private fun initViews() {
        Timber.tag(TAG).d("初始化界面视图")
        switchAutoStart = findViewById(R.id.switchAutoStart)
        btnNaviHud = findViewById(R.id.btnNaviHud)
        
        // 设置开关状态
        switchAutoStart.isChecked = PreferenceUtils.isAutoStartEnabled(this)
        Timber.tag(TAG).d("自启动开关状态: ${switchAutoStart.isChecked}")
    }

    private fun setupClickListeners() {
        Timber.tag(TAG).d("设置点击监听器")
        
        // 自启动开关监听
        switchAutoStart.setOnCheckedChangeListener { _, isChecked ->
            Timber.tag(TAG).i("自启动开关状态变更: $isChecked")
            PreferenceUtils.setAutoStartEnabled(this, isChecked)
        }

        // 启动导航HUD按钮 - 手动模式
        btnNaviHud.setOnClickListener {
            Timber.tag(TAG).i("点击启动导航HUD按钮(手动模式)")
            startHudDisplayActivity(false) // false表示手动启动
        }

        // 其他按钮监听保持原有逻辑...
        findViewById<Button>(R.id.btnNaviHudClose).setOnClickListener {
            Timber.tag(TAG).i("点击关闭导航HUD按钮")
            // 关闭导航HUD的逻辑
            HudDisplayActivity.closeHud(this)
        }

        findViewById<Button>(R.id.btnVideo).setOnClickListener {
            Timber.tag(TAG).i("点击视频按钮")
            startActivity(Intent(this, HudVideoActivity::class.java))
        }

        findViewById<Button>(R.id.btnSetting).setOnClickListener {
            Timber.tag(TAG).i("点击设置按钮")
            startActivity(Intent(this, SettingActivity::class.java))
        }
        
        // 开发模式按钮
        findViewById<Button>(R.id.btnTest)?.setOnClickListener {
            Timber.tag(TAG).i("点击测试按钮")
            startActivity(Intent(this, TestActivity::class.java))
        }
        
        findViewById<Button>(R.id.btnLog)?.setOnClickListener {
            Timber.tag(TAG).i("点击日志按钮")
            startActivity(Intent(this, LogFileListActivity::class.java))
        }
        
        // 颜色设置按钮
        findViewById<Button>(R.id.btnColorSettings)?.setOnClickListener {
            Timber.tag(TAG).i("点击颜色设置按钮")
            startActivity(Intent(this, LandColorSettingActivity::class.java))
        }
        
        // 颜色管理器测试按钮
        findViewById<Button>(R.id.btnTestColorManager)?.setOnClickListener {
            Timber.tag(TAG).i("测试颜色管理器日志")
            val colorManager = ColorPreferenceManager.getInstance(this)
            colorManager.getLandPrimaryColor()
            colorManager.getLandSecondaryColor()
            colorManager.setLandPrimaryColor(Color.BLUE)
            colorManager.setLandSecondaryColor(Color.GREEN)
            colorManager.resetToDefault()
        }
    }

    private fun checkAutoStart() {
        Timber.tag(TAG).d("检查自启动设置")
        // 只有在自启动开启且HUD服务没有运行时才执行
        if (PreferenceUtils.isAutoStartEnabled(this) && !HudDisplayActivity.isHudServiceRunning()) {
            Timber.tag(TAG).i("满足自启动条件，准备跳转")
            // 延迟一段时间再跳转，让界面先显示出来
            handler.postDelayed({
                startHudDisplayActivity(true) // true表示自启动模式
            }, 800)
        } else {
            Timber.tag(TAG).d("不满足自启动条件或HUD服务已在运行")
        }
    }

    private fun startHudDisplayActivity(isAutoStart: Boolean) {
        Timber.tag(TAG).d("启动HUD显示Activity - 自启动模式: $isAutoStart")
        if (Settings.canDrawOverlays(this)) {
            // 标记启动模式
            PreferenceUtils.setAutoStartMode(this, isAutoStart)
            
            // 使用新的启动方法，传递自启动模式
            HudDisplayActivity.startActivity(this, isAutoStart)
            
            if (isAutoStart) {
                Timber.tag(TAG).i("自启动模式，结束当前Activity")
                // 如果是自启动模式，立即finish当前页面
                finish()
            }
        } else {
            Timber.tag(TAG).w("缺少悬浮窗权限")
            Toast.makeText(this, "请先授予悬浮窗权限", Toast.LENGTH_LONG).show()
        }
    }
}
