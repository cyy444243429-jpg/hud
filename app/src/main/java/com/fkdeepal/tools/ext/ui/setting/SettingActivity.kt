package com.fkdeepal.tools.ext.ui.setting

import android.app.Activity
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Display
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
        // 修改：使用 Any 类型避免编译错误
        var hudAdapter: Any? = null

        fun startActivity(activity: Activity) {
            Timber.tag(TAG).d("启动设置Activity")
            val intent = Intent(activity, SettingActivity::class.java)
            activity.startActivity(intent)
        }
        
        // 修改：使用 Any 类型参数
        fun setHudAdapter(adapter: Any?) {
            hudAdapter = adapter
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
            
            // 新增：图标大小设置点击事件
            findPreference<Preference>("key_land_icon_size_pref")?.let { pref ->
                Timber.tag(TAG).d("初始化图标大小设置选项")
                
                // 设置当前值显示
                val currentSize = PreferenceUtils.getLandIconSize(requireContext())
                pref.summary = "当前尺寸: ${currentSize}px (30-80)"
                
                pref.onPreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
                    Timber.tag(TAG).i("点击图标大小设置")
                    showIconSizeDialog()
                    true
                }
            }
        }
        
        /**
         * 显示图标大小设置对话框
         */
        private fun showIconSizeDialog() {
            val currentSize = PreferenceUtils.getLandIconSize(requireContext())
            
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_icon_size, null)
            val seekBar = dialogView.findViewById<SeekBar>(R.id.seekBar_icon_size)
            val textValue = dialogView.findViewById<TextView>(R.id.text_size_value)
            
            // 设置 SeekBar 范围
            seekBar.min = 30
            seekBar.max = 80
            seekBar.progress = currentSize
            textValue.text = "${currentSize}px"
            
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    textValue.text = "${progress}px"
                }
                
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    Timber.tag(TAG).d("开始调整图标大小")
                }
                
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    val newSize = seekBar.progress
                    Timber.tag(TAG).i("图标大小调整完成: ${newSize}px")
                    
                    // 保存新的大小
                    PreferenceUtils.setLandIconSize(requireContext(), newSize)
                    
                    // 更新摘要
                    findPreference<Preference>("key_land_icon_size_pref")?.summary = "当前尺寸: ${newSize}px (30-80)"
                    
                    // 修改：使用反射调用刷新方法，避免编译错误
                    refreshHudAdapter(newSize)
                }
            })
            
            AlertDialog.Builder(requireContext())
                .setTitle("车道图标大小")
                .setView(dialogView)
                .setPositiveButton("确定") { dialog, _ -> 
                    Timber.tag(TAG).d("图标大小设置对话框确认")
                    dialog.dismiss() 
                }
                .setOnDismissListener {
                    Timber.tag(TAG).d("图标大小设置对话框关闭")
                }
                .show()
        }
        
        /**
         * 新增：使用反射安全地调用适配器刷新方法
         */
        private fun refreshHudAdapter(newSize: Int) {
            try {
                val adapter = SettingActivity.hudAdapter
                if (adapter != null) {
                    // 使用反射调用 refreshIconSizes 方法
                    val method = adapter.javaClass.getMethod("refreshIconSizes")
                    method.invoke(adapter)
                    Timber.tag(TAG).d("成功调用适配器刷新方法，新尺寸: ${newSize}px")
                } else {
                    Timber.tag(TAG).w("适配器为null，无法刷新图标尺寸")
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "调用适配器刷新方法失败")
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
