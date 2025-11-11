package com.fkdeepal.tools.ext.presentation

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.WindowManager
import com.fkdeepal.tools.ext.R

class HudPresentation(
    val mContext: Context,
    val mDisplay: Display): Presentation(mContext,mDisplay) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         window?.let {
             runCatching {
                 val layoutParams = it.attributes
                 layoutParams.type =WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                 layoutParams.width = mDisplay.width/2
                 it.attributes = layoutParams

             }

        }
        setContentView(R.layout.activity_hud)
    }

    override fun onDisplayRemoved() {
        super.onDisplayRemoved()
    }

    override fun onStop() {
        super.onStop()
    }
}