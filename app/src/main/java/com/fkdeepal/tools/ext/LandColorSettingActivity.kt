package com.fkdeepal.tools.ext

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.fkdeepal.tools.ext.databinding.ActivityLandColorSettingBinding
import com.fkdeepal.tools.ext.utils.ColorPreferenceManager
import timber.log.Timber

class LandColorSettingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLandColorSettingBinding
    private val colorManager by lazy { ColorPreferenceManager.getInstance(this) }
    
    private var currentPrimaryColor = 0
    private var currentSecondaryColor = 0
    
    companion object {
        private const val TAG = "LandColorSettingActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(TAG).d("Activity创建")
        binding = ActivityLandColorSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initColors()
        setupSeekBars()
        setupButtons()
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
    
    private fun initColors() {
        Timber.tag(TAG).d("初始化颜色设置")
        currentPrimaryColor = colorManager.getLandPrimaryColor()
        currentSecondaryColor = colorManager.getLandSecondaryColor()
        
        updateColorDisplays()
    }
    
    private fun setupSeekBars() {
        Timber.tag(TAG).d("设置颜色滑块")
        // 主颜色（灰色）RGB控制
        setupColorSeekBar(binding.seekBarPrimaryRed, binding.seekBarPrimaryGreen, binding.seekBarPrimaryBlue, 
                         currentPrimaryColor, true)
        
        // 次颜色（红色）RGB控制
        setupColorSeekBar(binding.seekBarSecondaryRed, binding.seekBarSecondaryGreen, binding.seekBarSecondaryBlue, 
                         currentSecondaryColor, false)
    }
    
    private fun setupColorSeekBar(redSeekBar: SeekBar, greenSeekBar: SeekBar, blueSeekBar: SeekBar, 
                                 color: Int, isPrimary: Boolean) {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        
        redSeekBar.progress = red
        greenSeekBar.progress = green
        blueSeekBar.progress = blue
        
        val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newColor = Color.rgb(
                        redSeekBar.progress,
                        greenSeekBar.progress,
                        blueSeekBar.progress
                    )
                    
                    if (isPrimary) {
                        currentPrimaryColor = newColor
                        binding.primaryColorDisplay.setBackgroundColor(newColor)
                        binding.tvPrimaryColorValue.text = String.format("#%06X", 0xFFFFFF and newColor)
                        Timber.tag(TAG).d("主颜色滑块变更: R=${redSeekBar.progress}, G=${greenSeekBar.progress}, B=${blueSeekBar.progress}")
                    } else {
                        currentSecondaryColor = newColor
                        binding.secondaryColorDisplay.setBackgroundColor(newColor)
                        binding.tvSecondaryColorValue.text = String.format("#%06X", 0xFFFFFF and newColor)
                        Timber.tag(TAG).d("次颜色滑块变更: R=${redSeekBar.progress}, G=${greenSeekBar.progress}, B=${blueSeekBar.progress}")
                    }
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Timber.tag(TAG).d("开始拖动颜色滑块")
            }
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Timber.tag(TAG).d("停止拖动颜色滑块")
            }
        }
        
        redSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        greenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        blueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
    }
    
    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            Timber.tag(TAG).i("点击保存按钮 - 主颜色: ${String.format("#%06X", 0xFFFFFF and currentPrimaryColor)}, 次颜色: ${String.format("#%06X", 0xFFFFFF and currentSecondaryColor)}")
            colorManager.setLandPrimaryColor(currentPrimaryColor)
            colorManager.setLandSecondaryColor(currentSecondaryColor)
            finish()
        }
        
        binding.btnReset.setOnClickListener {
            Timber.tag(TAG).i("点击重置按钮")
            colorManager.resetToDefault()
            initColors()
            setupSeekBars()
        }
        
        binding.btnCancel.setOnClickListener {
            Timber.tag(TAG).i("点击取消按钮")
            finish()
        }
    }
    
    private fun updateColorDisplays() {
        Timber.tag(TAG).d("更新颜色显示 - 主颜色: ${String.format("#%06X", 0xFFFFFF and currentPrimaryColor)}, 次颜色: ${String.format("#%06X", 0xFFFFFF and currentSecondaryColor)}")
        binding.primaryColorDisplay.setBackgroundColor(currentPrimaryColor)
        binding.secondaryColorDisplay.setBackgroundColor(currentSecondaryColor)
        
        binding.tvPrimaryColorValue.text = String.format("#%06X", 0xFFFFFF and currentPrimaryColor)
        binding.tvSecondaryColorValue.text = String.format("#%06X", 0xFFFFFF and currentSecondaryColor)
    }
}
