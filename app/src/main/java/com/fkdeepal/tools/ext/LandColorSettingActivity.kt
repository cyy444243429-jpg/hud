package com.fkdeepal.tools.ext

import android.view.LayoutInflater
import android.view.View
import com.fkdeepal.tools.ext.base.BaseActivity
import com.fkdeepal.tools.ext.databinding.ActivityLandColorSettingBinding
import com.fkdeepal.tools.ext.utils.ColorPreferenceManager
import android.graphics.Color
import android.os.Bundle
import timber.log.Timber

class LandColorSettingActivity : BaseActivity<ActivityLandColorSettingBinding>() {
    
    companion object {
        private const val TAG = "LandColorSettingActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(TAG).d("Activity创建")
        // 设置标题
        supportActionBar?.title = "车道图标颜色设置"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    override fun initViewBinding(layoutInflater: LayoutInflater): ActivityLandColorSettingBinding? {
        Timber.tag(TAG).d("初始化视图绑定")
        return ActivityLandColorSettingBinding.inflate(layoutInflater)
    }
    
    override fun initViews() {
        Timber.tag(TAG).d("初始化视图")
        // 初始化颜色选择器
        initColorPickers()
        
        // 设置重置按钮
        mViewBinding.btnResetColors.setOnClickListener {
            Timber.tag(TAG).i("点击重置颜色按钮")
            ColorPreferenceManager.resetColors()
            initColorPickers() // 重新初始化
        }
        
        // 设置保存按钮
        mViewBinding.btnSaveColors.setOnClickListener {
            Timber.tag(TAG).i("点击完成按钮")
            // 颜色已经实时保存，这里只是关闭页面
            finish()
        }
    }
    
    private fun initColorPickers() {
        Timber.tag(TAG).d("初始化颜色选择器")
        val primaryColor = ColorPreferenceManager.getPrimaryColor()
        val secondaryColor = ColorPreferenceManager.getSecondaryColor()
        
        Timber.tag(TAG).d("当前颜色 - 主色: ${String.format("#%06X", 0xFFFFFF and primaryColor)}, 次色: ${String.format("#%06X", 0xFFFFFF and secondaryColor)}")
        
        // 主颜色选择器
        mViewBinding.primaryColorPicker.setColor(primaryColor)
        mViewBinding.primaryColorPreview.setBackgroundColor(primaryColor)
        mViewBinding.primaryColorPicker.setOnColorChangeListener { color ->
            Timber.tag(TAG).d("主颜色变更: ${String.format("#%06X", 0xFFFFFF and color)}")
            mViewBinding.primaryColorPreview.setBackgroundColor(color)
            ColorPreferenceManager.setPrimaryColor(color)
            updateColorValueDisplays(color, secondaryColor)
        }
        
        // 次颜色选择器
        mViewBinding.secondaryColorPicker.setColor(secondaryColor)
        mViewBinding.secondaryColorPreview.setBackgroundColor(secondaryColor)
        mViewBinding.secondaryColorPicker.setOnColorChangeListener { color ->
            Timber.tag(TAG).d("次颜色变更: ${String.format("#%06X", 0xFFFFFF and color)}")
            mViewBinding.secondaryColorPreview.setBackgroundColor(color)
            ColorPreferenceManager.setSecondaryColor(color)
            updateColorValueDisplays(primaryColor, color)
        }
        
        // 更新颜色值显示
        updateColorValueDisplays(primaryColor, secondaryColor)
    }
    
    private fun updateColorValueDisplays(primaryColor: Int, secondaryColor: Int) {
        mViewBinding.tvPrimaryColorValue.text = String.format("#%06X", 0xFFFFFF and primaryColor)
        mViewBinding.tvSecondaryColorValue.text = String.format("#%06X", 0xFFFFFF and secondaryColor)
        Timber.tag(TAG).d("更新颜色显示 - 主色: ${mViewBinding.tvPrimaryColorValue.text}, 次色: ${mViewBinding.tvSecondaryColorValue.text}")
    }
    
    override fun onViewClick(v: View) {
        // 处理其他点击事件
    }
    
    override fun onResume() {
        super.onResume()
        Timber.tag(TAG).d("Activity恢复")
    }
    
    override fun onPause() {
        super.onPause()
        Timber.tag(TAG).d("Activity暂停")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(TAG).d("Activity销毁")
    }
}
