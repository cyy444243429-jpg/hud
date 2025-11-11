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
import com.fkdeepal.tools.ext.ui.video.HudVideoActivity
import com.fkdeepal.tools.ext.ui.video.VideoPresentation
import com.fkdeepal.tools.ext.utils.AppUtils
import java.text.SimpleDateFormat
import java.util.Locale


class SettingActivity: AppCompatActivity() {
    companion object{

        fun startActivity(activity: Activity) {
            val intent = Intent(activity, SettingActivity::class.java)
            activity.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    class SettingFragment : PreferenceFragmentCompat() {
        private val mDisplayManager by lazy { ContextCompat.getSystemService<DisplayManager>(requireContext(), DisplayManager::class.java) }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.setting_user, rootKey)
            findPreference<ListPreference>("key_setting_hud_display_id")?.let {
                val displayData = arrayListOf<Display>()
                mDisplayManager?.let {
                    displayData.addAll(it.displays)
                }
                it.entries = displayData.map { "${it.displayId} - ${it.width}x${it.height} - ${if (it.state == Display.STATE_ON) "可用" else "不可用"}" }.toTypedArray()
                it.entryValues  =  displayData.map { "${it.displayId}" }.toTypedArray()
            }
            val swDebug = findPreference<SwitchPreferenceCompat>("key_is_debug")
                swDebug?.let {
                    it.onPreferenceChangeListener = object : Preference.OnPreferenceChangeListener{
                        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
                            if (newValue is Boolean){
                                AppUtils.isDebug = newValue
                            }
                            return true; // 返回 true 表示接受新值
                        }
                    }
                if (AppUtils.isDebug){
                    it?.isVisible = true
                }
            }

            findPreference<PreferenceScreen>("key_build_time")?.let{
                val dateFormat = SimpleDateFormat("yyyyMMddHHmm", Locale.US)
                val buildDate: String = dateFormat.format(BuildConfig.BUILD_TIME_MILLIS)
                it.onPreferenceClickListener = object  : Preference.OnPreferenceClickListener{
                    var mClickTime: Int = 0
                    var mFirstClickTime: Long = 0

                    override fun onPreferenceClick(preference: Preference): Boolean {
                        if (mClickTime == 0) {
                            mFirstClickTime = System.currentTimeMillis()
                        }
                        ++mClickTime
                        if (mClickTime == 10) {
                            if (System.currentTimeMillis() - mFirstClickTime < 4 * 1000) {
                                swDebug?.isVisible = true
                            }
                            mClickTime = 0
                        }else{
                            if (System.currentTimeMillis() - mFirstClickTime > 4 * 1000) {
                                mClickTime = 0
                            }
                        }

                        return false
                    }
                }
                it.summary = buildDate
            }
        }

    }
}