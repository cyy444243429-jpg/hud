package com.fkdeepal.tools.ext.ui.audio

import android.media.AudioManager
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.fkdeepal.tools.ext.base.BaseActivity
import com.fkdeepal.tools.ext.databinding.ActivityAudioPlaybackBinding

class AudioPlaybackActivity : BaseActivity<ActivityAudioPlaybackBinding>() {
    private val mAudioManager by lazy {
        ContextCompat.getSystemServiceName(mActivity, AudioManager::class.java)!!
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): ActivityAudioPlaybackBinding? = ActivityAudioPlaybackBinding.inflate(layoutInflater)
    override fun initViews() {

    }

    override fun onViewClick(v: View) {
    }
}