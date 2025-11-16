package com.fkdeepal.tools.ext

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.fkdeepal.tools.ext.databinding.ActivityLandColorSettingBinding
import com.fkdeepal.tools.ext.utils.ColorPreferenceManager

class LandColorSettingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLandColorSettingBinding
    private val colorManager by lazy { ColorPreferenceManager.getInstance(this) }
    
    private var currentPrimaryColor = 0
    private var currentSecondaryColor = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandColorSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initColors()
        setupSeekBars()
        setupButtons()
    }
    
    private fun initColors() {
        currentPrimaryColor = colorManager.getLandPrimaryColor()
        currentSecondaryColor = colorManager.getLandSecondaryColor()
        
        updateColorDisplays()
    }
    
    private fun setupSeekBars() {
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
                    } else {
                        currentSecondaryColor = newColor
                        binding.secondaryColorDisplay.setBackgroundColor(newColor)
                        binding.tvSecondaryColorValue.text = String.format("#%06X", 0xFFFFFF and newColor)
                    }
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
        
        redSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        greenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        blueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
    }
    
    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            colorManager.setLandPrimaryColor(currentPrimaryColor)
            colorManager.setLandSecondaryColor(currentSecondaryColor)
            finish()
        }
        
        binding.btnReset.setOnClickListener {
            colorManager.resetToDefault()
            initColors()
            setupSeekBars()
        }
        
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun updateColorDisplays() {
        binding.primaryColorDisplay.setBackgroundColor(currentPrimaryColor)
        binding.secondaryColorDisplay.setBackgroundColor(currentSecondaryColor)
        
        binding.tvPrimaryColorValue.text = String.format("#%06X", 0xFFFFFF and currentPrimaryColor)
        binding.tvSecondaryColorValue.text = String.format("#%06X", 0xFFFFFF and currentSecondaryColor)
    }
}
