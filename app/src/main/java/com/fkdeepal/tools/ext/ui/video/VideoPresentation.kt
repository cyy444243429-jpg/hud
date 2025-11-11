package com.fkdeepal.tools.ext.ui.video

import android.app.Presentation
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Display
import com.fkdeepal.tools.ext.databinding.PresentationVideoBinding

class VideoPresentation(
    val mContext: Context,
    val mDisplay: Display) : Presentation(mContext, mDisplay) {
    private val mViewBinding: PresentationVideoBinding = PresentationVideoBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
        mViewBinding.video.setOnPreparedListener {

        }
        mViewBinding.video.setOnCompletionListener {
            dismiss()
        }
    }
    fun pause(){
        mViewBinding.video.pause()
    }
    fun resume(){
        mViewBinding.video.start()
    }
    fun stop(){
        mViewBinding.video.stopPlayback()
    }
    fun setVideoUrl(uri: Uri){
        mViewBinding.apply {
            video.pause()
            video.setVideoURI(uri)
            video.start()
        }
    }

    override fun dismiss() {
        runCatching {
            mViewBinding.video.suspend()
        }
        super.dismiss()

    }
}