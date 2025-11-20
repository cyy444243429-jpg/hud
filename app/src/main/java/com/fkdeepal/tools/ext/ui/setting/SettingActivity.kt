package com.fkdeepal.tools.ext.ui.setting

import android.app.Activity
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.Display
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreferenceCompat
import com.fkdeepal.tools.ext.BuildConfig
import com.fkdeepal.tools.ext.R
import com.fkdeepal.tools.ext.LandColorSettingActivity
import com.fkdeepal.tools.ext.ui.video.HudVideoActivity
import com.fkdeepal.tools.ext.ui.video.VideoPresentation
import com.fkdeepal.tools.ext.utils.AppUtils
import com.fkdeepal.tools.ext.utils.PreferenceUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

class SettingActivity: AppCompatActivity() {
    companion object{
        private const val TAG = "SettingActivity"

        fun startActivity(activity: Activity) {
            Timber.tag(TAG).d("启动设置Activity")
            val intent = Intent(activity, SettingActivity::class.java)
            activity.startActivity(intent)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(TAG).d("SettingActivity创建")
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    override fun onResume() {
        super.onResume()
        Timber.tag(TAG).d("SettingActivity恢复")
    }
    
    override fun onPause() {
        super.onPause()
        Timber.tag(TAG).d("SettingActivity暂停")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(TAG).d("SettingActivity销毁")
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Timber.tag(TAG).d("点击返回按钮")
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    class SettingFragment : PreferenceFragmentCompat() {
        private val mDisplayManager by lazy { 
            ContextCompat.getSystemService<DisplayManager>(requireContext(), DisplayManager::class.java) 
        }
        
        // 修复：将点击计数变量移到类级别
        private var mClickTime: Int = 0
        private var mFirstClickTime: Long = 0
        
        companion object {
            private const val TAG = "SettingFragment"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            Timber.tag(TAG).d("创建首选项")
            setPreferencesFromResource(R.xml.setting_user, rootKey)
            
            findPreference<ListPreference>("key_setting_hud_display_id")?.let {
                Timber.tag(TAG).d("初始化HUD显示设置")
                val displayData = arrayListOf<Display>()
                mDisplayManager?.let { manager ->
                    displayData.addAll(manager.displays)
                }
                it.entries = displayData.map { display -> 
                    "${display.displayId} - ${display.width}x${display.height} - ${if (display.state == Display.STATE_ON) "可用" else "不可用"}" 
                }.toTypedArray()
                it.entryValues = displayData.map { "${it.displayId}" }.toTypedArray()
            }
            
            val swDebug = findPreference<SwitchPreferenceCompat>("key_is_debug")
            swDebug?.let {
                Timber.tag(TAG).d("初始化调试开关")
                it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                    if (newValue is Boolean) {
                        Timber.tag(TAG).i("调试模式变更: $newValue")
                        AppUtils.isDebug = newValue
                    }
                    true
                }
                if (AppUtils.isDebug) {
                    it.isVisible = true
                }
            }

            findPreference<PreferenceScreen>("key_build_time")?.let {
                Timber.tag(TAG).d("初始化构建时间")
                
                // 安全地获取构建时间，如果不存在则使用当前时间
                val buildTime = try {
                    BuildConfig.BUILD_TIME_MILLIS
                } catch (e: Exception) {
                    Timber.tag(TAG).w("BUILD_TIME_MILLIS 未定义，使用当前时间")
                    System.currentTimeMillis()
                }
                
                val dateFormat = SimpleDateFormat("yyyyMMddHHmm", Locale.US)
                val buildDate: String = dateFormat.format(buildTime)
                
                // 修复：使用类级别的变量
                it.onPreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
                    Timber.tag(TAG).d("点击构建时间，当前点击次数: $mClickTime")
                    
                    if (mClickTime == 0) {
                        mFirstClickTime = System.currentTimeMillis()
                    }
                    ++mClickTime
                    
                    if (mClickTime == 10) {
                        if (System.currentTimeMillis() - mFirstClickTime < 4 * 1000) {
                            Timber.tag(TAG).i("快速点击10次，显示调试选项")
                            swDebug?.isVisible = true
                        }
                        mClickTime = 0
                    } else {
                        if (System.currentTimeMillis() - mFirstClickTime > 4 * 1000) {
                            Timber.tag(TAG).d("点击超时，重置计数")
                            mClickTime = 0
                        }
                    }
                    false
                }
                it.summary = buildDate
            }
            
            // 颜色设置点击事件
            findPreference<Preference>("key_land_color_setting")?.let {
                Timber.tag(TAG).d("初始化颜色设置选项")
                it.onPreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
                    Timber.tag(TAG).i("点击颜色设置选项")
                    startActivity(Intent(requireContext(), LandColorSettingActivity::class.java))
                    true
                }
            }
            
            // ========== 新增：图标大小滑块监听 ==========
            findPreference<androidx.preference.SeekBarPreference>("key_land_icon_size")?.let { seekBarPref ->
                Timber.tag(TAG).d("初始化图标大小滑块")
                
                // 设置当前值显示
                val currentSize = PreferenceUtils.getLandIconSize(requireContext())
                seekBarPref.summary = "当前尺寸: ${currentSize}px"
                
                seekBarPref.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                    if (newValue is Int) {
                        Timber.tag(TAG).i("车道图标大小变更: ${newValue}px")
                        
                        // 保存新的大小
                        PreferenceUtils.setLandIconSize(requireContext(), newValue)
                        
                        // 更新摘要显示
                        seekBarPref.summary = "当前尺寸: ${newValue}px"
                        
                        // 发送广播通知HUD界面更新图标尺寸
                        sendIconSizeChangedBroadcast(newValue)
                    }
                    true
                }
            }
        }
        
        /**
         * 新增：发送图标大小变更广播
         */
        private fun sendIconSizeChangedBroadcast(newSize: Int) {
            Timber.tag(TAG).d("发送图标大小变更广播: ${newSize}px")
            try {
                // 这里可以发送广播通知HUD界面刷新
                // 或者通过其他方式通知适配器刷新
                val intent = Intent("ACTION_LAND_ICON_SIZE_CHANGED")
                intent.putExtra("icon_size", newSize)
                requireContext().sendBroadcast(intent)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "发送图标大小变更广播失败")
            }
        }
        
        override fun onResume() {
            super.onResume()
            Timber.tag(TAG).d("SettingFragment恢复")
        }
        
        override fun onPause() {
            super.onPause()
            Timber.tag(TAG).d("SettingFragment暂停")
        }
        
        override fun onDestroy() {
            super.onDestroy()
            Timber.tag(TAG).d("SettingFragment销毁")
        }
    }
}
