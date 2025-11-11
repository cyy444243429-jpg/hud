package com.fkdeepal.tools.ext.ui

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import com.fkdeepal.tools.ext.AmapFloatManager
import com.fkdeepal.tools.ext.BuildConfig
import com.fkdeepal.tools.ext.R
import com.fkdeepal.tools.ext.base.BaseActivity
import com.fkdeepal.tools.ext.databinding.ActivityMainBinding
import com.fkdeepal.tools.ext.presentation.HudPresentation
import com.fkdeepal.tools.ext.ui.log.LogFileListActivity
import com.fkdeepal.tools.ext.ui.setting.SettingActivity
import com.fkdeepal.tools.ext.ui.test.TestActivity
import com.fkdeepal.tools.ext.ui.video.HudVideoActivity
import com.fkdeepal.tools.ext.utils.AppUtils
import java.text.SimpleDateFormat
import java.util.Locale

class HomeActivity: BaseActivity<ActivityMainBinding>() {
    override fun initViews() {
        if (!isTaskRoot) {
            val intent: Intent? = intent // 如果当前 Activity 是通过桌面图标启动进入的
            if (((intent != null) && (intent.hasCategory(Intent.CATEGORY_LAUNCHER) || (Intent.ACTION_MAIN == intent.action)) )) { // 对当前 Activity 执行销毁操作，避免重复实例化入口
                finish()
                return
            }
        }
        mViewBinding.apply {
            setOnClickListener(btnTest,btnNaviHud,btnLog,btnNaviHudClose,btnVideo,btnSetting)
            val dateFormat = SimpleDateFormat("yyyyMMddHHmm", Locale.US)
            val buildDate: String = dateFormat.format(BuildConfig.BUILD_TIME_MILLIS)
            tvBuildTime.setText(buildDate)
            if (AppUtils.isDebug){
                layoutDev.visibility = View.VISIBLE
            }
        }
    }

    override fun isDisplayHomeAsUpEnabled(): Boolean  = false
    override fun initViewBinding(layoutInflater: LayoutInflater): ActivityMainBinding?  = ActivityMainBinding.inflate(layoutInflater)
    override fun onViewClick(v: View) {
        mViewBinding.apply {
            when(v){
                btnTest-> TestActivity.startActivity(mActivity)
                btnNaviHudClose-> AmapFloatManager.hideHudFloat()
                btnVideo-> HudVideoActivity.startActivity(mActivity)
                btnLog-> LogFileListActivity.startActivity(mActivity)
                btnSetting-> SettingActivity.startActivity(mActivity)
                btnNaviHud->{
                    if (!Settings.canDrawOverlays(mActivity)) {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                        startActivityForResult(intent, 1)
                        return
                    }
                    HudDisplayActivity.startActivity(mActivity)
                }
            }
        }
    }
}